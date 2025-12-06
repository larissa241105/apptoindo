package com.example.toindoapp.viewmodel.eventos // Ou o pacote onde sua ViewModel está localizada

import android.util.Log // Import adicionado
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.toindoapp.data.eventos.Convite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Seu data class ParticipantesUiState atualizado
data class ParticipantesUiState(
    val isLoading: Boolean = true,
    val confirmados: List<ParticipanteDisplay> = emptyList(),
    val pendentes: List<ParticipanteDisplay> = emptyList(),
    val recusados: List<ParticipanteDisplay> = emptyList(),
    val error: String? = null,
    val isUserCreator: Boolean = false,
)

// Coloque esta classe fora do seu ViewModel, no mesmo arquivo ou em um arquivo de models
data class ParticipanteDisplay(
    val userId: String = "",
    val nome: String = "Carregando...",
    val status: String = "",

)

class ParticipantesViewModel(private val eventoId: String, private val creatorId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(ParticipantesUiState())
    val uiState = _uiState.asStateFlow()
    private var participantesListener: ListenerRegistration? = null

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val isCreator = (currentUser?.uid == creatorId)

        _uiState.update { it.copy(isUserCreator = isCreator) }
        fetchParticipantes()
    }

    private fun fetchParticipantes() {
        _uiState.update { it.copy(isLoading = true) }

        val query = FirebaseFirestore.getInstance().collection("convites")
            .whereEqualTo("eventoId", eventoId)

        participantesListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                _uiState.update { it.copy(isLoading = false, error = error.message) }
                return@addSnapshotListener
            }

            if (snapshot == null || snapshot.isEmpty) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        confirmados = emptyList(),
                        pendentes = emptyList(),
                        recusados = emptyList()
                    )
                }
                return@addSnapshotListener
            }

            val todosOsConvites = snapshot.toObjects(Convite::class.java)

            val userIds = todosOsConvites.map { it.convidadoUid }.distinct()

            if (userIds.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, confirmados = emptyList(), pendentes = emptyList()) }
                return@addSnapshotListener
            }

            FirebaseFirestore.getInstance().collection("users")
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .addOnSuccessListener { userSnapshot ->

                    val userMap = userSnapshot.documents.associateBy(
                        { it.id }, // Chave: ID do usuário
                        { it.getString("nome") ?: "Nome Desconhecido" } // Valor: Nome
                    )

                    val participantesDisplayList = todosOsConvites.map { convite ->
                        ParticipanteDisplay(
                            userId = convite.convidadoUid,
                            nome = userMap[convite.convidadoUid] ?: "Nome não encontrado",
                            status = convite.status
                        )
                    }

                    val agrupadosPorStatus = participantesDisplayList.groupBy { it.status }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            confirmados = agrupadosPorStatus["ACEITO"] ?: emptyList(),
                            pendentes = agrupadosPorStatus["PENDENTE"] ?: emptyList(),
                            recusados = agrupadosPorStatus["RECUSADO"] ?: emptyList()
                        )
                    }
                }
                .addOnFailureListener { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Erro ao buscar nomes: ${e.message}") }
                }
        }
    }
    override fun onCleared() {
        super.onCleared()
        participantesListener?.remove()
    }

    class Factory(private val eventoId: String, private val creatorId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ParticipantesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ParticipantesViewModel(eventoId, creatorId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}