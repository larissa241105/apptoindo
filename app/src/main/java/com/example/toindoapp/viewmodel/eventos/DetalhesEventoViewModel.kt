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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Adicione um estado de ação para a UI saber o que fazer após a exclusão
enum class EventoActionState { IDLE, DELETED }

data class DetalhesEventoUiState(
    val isLoading: Boolean = true,
    val isSendingInvite: Boolean = false, // Novo: para o feedback do botão de convite
    val evento: Evento? = null,
    val isUserCreator: Boolean = false,
    val error: String? = null,
    val actionState: EventoActionState = EventoActionState.IDLE
)

class DetalhesEventoViewModel(private val eventoId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalhesEventoUiState())
    val uiState = _uiState.asStateFlow()

    // StateFlow separado para mensagens de status (Snackbar)
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

                val evento = document.toObject(Evento::class.java)?.copy(id = document.id)
                val userId = Firebase.auth.currentUser?.uid
                val isCreator = (userId != null && evento?.creatorId == userId)

                _uiState.update {
                    it.copy(isLoading = false, evento = evento, isUserCreator = isCreator)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // --- LÓGICA DE CONVITE ---
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
                    nomeDoEvento = eventoAtual.nome, // Supondo que seu Evento tem um campo 'name'
                    quemConvidou = usuarioAtual.displayName ?: "Criador do Evento",
                    convidadoUid = convidadoUid.trim(), // Remove espaços em branco
                    eventoId = eventoId,
                    status = "PENDENTE"
                )

                // Salva o novo documento na coleção "convites"
                Firebase.firestore.collection("convites").add(novoConvite).await()

                // Atualiza o status com sucesso
                _conviteStatus.value = "Convite enviado com sucesso!"

            } catch (e: Exception) {
                // Atualiza o status com erro
                _conviteStatus.value = "Falha ao enviar convite: ${e.message}"
            } finally {
                // Finaliza o estado de carregamento
                _uiState.update { it.copy(isSendingInvite = false) }
            }
        }
    }

    /**
     * Limpa a mensagem de status para que o Snackbar não apareça novamente.
     */
    fun clearConviteStatus() {
        _conviteStatus.value = null
    }


    // --- LÓGICA DE EXCLUSÃO ---
    fun deleteEvento() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // re-usando o isLoading geral
            try {
                Firebase.firestore.collection("eventos").document(eventoId).delete().await()
                _uiState.update { it.copy(actionState = EventoActionState.DELETED, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Falha ao excluir o evento: ${e.message}", isLoading = false) }
            }
        }
    }

    // --- Factory para instanciar a ViewModel com eventoId ---
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