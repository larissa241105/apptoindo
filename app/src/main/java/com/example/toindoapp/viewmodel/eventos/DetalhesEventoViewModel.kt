package com.example.toindoapp.viewmodel

import Evento
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.toindoapp.data.eventos.Convite
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class EventoActionState { IDLE, DELETED }

data class DetalhesEventoUiState(
    val isLoading: Boolean = true,
    val isSendingInvite: Boolean = false,
    val evento: Evento? = null,
    val isUserCreator: Boolean = false,
    val error: String? = null,
    val actionState: EventoActionState = EventoActionState.IDLE,
    val isUserParticipating: Boolean = false,
    val isLeavingEvent: Boolean = false,
    val isJoiningEvent: Boolean = false
)

data class Convite(
    val nomeDoEvento: String = "",
    val quemConvidou: String = "",
    val convidadoUid: String = "",
    val eventoId: String = "",
    val status: String = "PENDENTE"
)


class DetalhesEventoViewModel(private val eventoId: String) : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(DetalhesEventoUiState())
    val uiState = _uiState.asStateFlow()

    private val _conviteStatus = MutableStateFlow<String?>(null)
    val conviteStatus = _conviteStatus.asStateFlow()

    init {
        fetchEvento()
    }

    private fun fetchEvento() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val document = Firebase.firestore.collection("eventos")
                    .document(eventoId)
                    .get()
                    .await()

                if (!document.exists()) {
                    _uiState.update { it.copy(isLoading = false, error = "Evento não encontrado.") }
                    return@launch
                }


                val eventoObj = document.toObject(Evento::class.java)
                val userId = Firebase.auth.currentUser?.uid

                val publicoFromFirebase = document.getBoolean("publico") ?: true
                val isGratuitoFromFirebase = document.getBoolean("isGratuito") ?: false
                val contagem = document.getLong("participantesCount")?.toInt() ?: 0
                val participantes = document.get("participantIds") as? List<String> ?: emptyList()

                val isCreator = (userId != null && eventoObj?.creatorId == userId)

                var isParticipando = false
                if (userId != null) {
                    val conviteQuery = Firebase.firestore.collection("convites")
                        .whereEqualTo("eventoId", eventoId)
                        .whereEqualTo("convidadoUid", userId)
                        .whereEqualTo("status", "ACEITO")
                        .limit(1)
                        .get()
                        .await()
                    isParticipando = !conviteQuery.isEmpty
                }

                val eventoFinal = eventoObj?.copy(
                    id = document.id,
                    publico = publicoFromFirebase,
                    isGratuito = isGratuitoFromFirebase,
                    participantesCount = contagem
                )




                _uiState.update {
                    it.copy(
                        isLoading = false,
                        evento = eventoFinal,
                        isUserCreator = isCreator,
                        isUserParticipating = isParticipando
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }


    fun enviarConvite(convidadoUid: String) {
        viewModelScope.launch {
            // Validações iniciais
            val eventoAtual = _uiState.value.evento
            val usuarioAtual = Firebase.auth.currentUser
            if (eventoAtual == null || usuarioAtual == null) {
                _conviteStatus.value = "Erro: Dados do evento ou usuário indisponíveis."
                return@launch
            }
            if (convidadoUid.isBlank()) {
                _conviteStatus.value = "Por favor, insira um UID."
                return@launch
            }
            if (convidadoUid == usuarioAtual.uid) {
                _conviteStatus.value = "Você não pode convidar a si mesmo."
                return@launch
            }

            // Inicia o estado de carregamento
            _uiState.update { it.copy(isSendingInvite = true) }

            try {
                // Cria o objeto do convite
                val novoConvite = Convite(
                    nomeDoEvento = eventoAtual.nome,
                    quemConvidou = usuarioAtual.displayName ?: "Criador do Evento",
                    convidadoUid = convidadoUid.trim(),
                    eventoId = eventoId,
                    status = "PENDENTE"
                )

                Firebase.firestore.collection("convites").add(novoConvite).await()


                _conviteStatus.value = "Convite enviado com sucesso!"

            } catch (e: Exception) {

                _conviteStatus.value = "Falha ao enviar convite: ${e.message}"
            } finally {

                _uiState.update { it.copy(isSendingInvite = false) }
            }
        }
    }


    fun participarEvento() {
        if (_uiState.value.isJoiningEvent) return

        val userId = Firebase.auth.currentUser?.uid
        val evento = _uiState.value.evento

        if (userId == null || evento == null) {
            _uiState.update { it.copy(error = "Erro: Usuário ou evento não encontrado.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isJoiningEvent = true) }
            try {
                val convitesRef = Firebase.firestore.collection("convites")
                val eventoRef = Firebase.firestore.collection("eventos").document(evento.id)

                val query = convitesRef
                    .whereEqualTo("eventoId", evento.id)
                    .whereEqualTo("convidadoUid", userId)
                    .limit(1)
                    .get()
                    .await()

                var jaEstavaAceito = false

                if (query.isEmpty) {
                    val novoConvite = Convite(
                        nomeDoEvento = evento.nome,
                        quemConvidou = "Participação Pública",
                        convidadoUid = userId,
                        eventoId = evento.id,
                        status = "ACEITO"
                    )
                    convitesRef.add(novoConvite).await()

                } else {

                    val docId = query.documents.first().id
                    val statusAtual = query.documents.first().getString("status")

                    if (statusAtual == "ACEITO") {
                        jaEstavaAceito = true
                    } else {
                        convitesRef.document(docId).update("status", "ACEITO").await()
                    }
                }

                if (!jaEstavaAceito) {
                    eventoRef.update("participantesCount", FieldValue.increment(1)).await()
                }

                // 4. Atualiza a UI
                _uiState.update {
                    it.copy(
                        isJoiningEvent = false,
                        isUserParticipating = true)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isJoiningEvent = false,
                        error = "Falha ao participar: ${e.message}"
                    )
                }
            }
        }
    }



    fun cancelarParticipacao() {
        // 0. Prevenção de clique duplo
        if (_uiState.value.isLeavingEvent) return

        val userId = Firebase.auth.currentUser?.uid
        val evento = _uiState.value.evento

        if (userId == null || evento == null) {
            _uiState.update { it.copy(error = "Erro: Usuário ou evento não encontrado.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLeavingEvent = true) } // 1. Mostra loading
            try {
                val convitesRef = Firebase.firestore.collection("convites")
                val eventoRef = Firebase.firestore.collection("eventos").document(evento.id)


                val query = convitesRef
                    .whereEqualTo("eventoId", evento.id)
                    .whereEqualTo("convidadoUid", userId)
                    .limit(1)
                    .get()
                    .await()

                if (query.isEmpty) {

                    _uiState.update { it.copy(isLeavingEvent = false, isUserParticipating = false) }
                    return@launch
                }

                val doc = query.documents.first()
                val docId = doc.id
                val statusAtual = doc.getString("status")

                if (statusAtual == "ACEITO") {

                    convitesRef.document(docId).update("status", "RECUSADO").await()

                    eventoRef.update("participantesCount", FieldValue.increment(-1)).await()

                } else {

                    if (statusAtual != "RECUSADO") {
                        convitesRef.document(docId).update("status", "RECUSADO").await()
                    }
                }

                _uiState.update {
                    it.copy(
                        isLeavingEvent = false,
                        isUserParticipating = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLeavingEvent = false,
                        error = "Falha ao cancelar: ${e.message}"
                    )
                }
            }
        }
    }



    fun clearConviteStatus() {
        _conviteStatus.value = null
    }


    fun deleteEvento() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                Firebase.firestore.collection("eventos").document(eventoId).delete().await()
                _uiState.update { it.copy(actionState = EventoActionState.DELETED, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Falha ao excluir o evento: ${e.message}", isLoading = false) }
            }
        }
    }


    class Factory(private val eventoId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetalhesEventoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetalhesEventoViewModel(eventoId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


