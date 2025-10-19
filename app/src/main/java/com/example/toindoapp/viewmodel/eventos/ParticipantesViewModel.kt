package com.example.toindoapp.viewmodel.eventos // Ou o pacote onde sua ViewModel está localizada

import android.util.Log // Import adicionado
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.toindoapp.data.eventos.Convite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Define a estrutura de dados para o estado da tela de Participantes.
 * A UI observa este objeto para se redesenhar quando os dados mudam.
 */
data class ParticipantesUiState(
    val isLoading: Boolean = true,
    val confirmados: List<Convite> = emptyList(),
    val pendentes: List<Convite> = emptyList(),
    val recusados: List<Convite> = emptyList(),
    val error: String? = null,
    val isUserCreator: Boolean = false,
)

/**
 * ViewModel responsável por gerenciar a lógica da tela de participantes.
 *
 * Responsabilidades:
 * 1. Receber o ID de um evento específico.
 * 2. Buscar em tempo real todos os convites associados a esse evento no Firestore.
 * 3. Agrupar os convites por status (aceito, pendente, recusado).
 * 4. Expor o estado (UiState) para a UI observar.
 * 5. Limpar os listeners do Firestore quando a ViewModel for destruída.
 */
class ParticipantesViewModel(private val eventoId: String, private val creatorId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(ParticipantesUiState())
    val uiState = _uiState.asStateFlow()
    private var participantesListener: ListenerRegistration? = null

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val isCreator = (currentUser?.uid == creatorId)

        // Bloco de Logs para Depuração
        Log.d("DebugParticipantes", "---------------------------------")
        Log.d("DebugParticipantes", "Verificando se é o criador...")
        Log.d("DebugParticipantes", "UID do Usuário Logado: ${currentUser?.uid}")
        Log.d("DebugParticipantes", "ID do Criador Recebido: $creatorId")
        Log.d("DebugParticipantes", "Os IDs são iguais? $isCreator")
        Log.d("DebugParticipantes", "---------------------------------")

        _uiState.update { it.copy(isUserCreator = isCreator) }
        fetchParticipantes()
    }

    private fun fetchParticipantes() {
        _uiState.update { it.copy(isLoading = true) }

        val query = FirebaseFirestore.getInstance().collection("convites")
            .whereEqualTo("eventoId", eventoId)

        participantesListener = query.addSnapshotListener { snapshot, error ->
            // ... (código de tratamento de erro)

            if (snapshot != null) {
                val todosOsConvites = snapshot.toObjects(Convite::class.java)
                val agrupadosPorStatus = todosOsConvites.groupBy { it.status }

                val listaDePendentes = agrupadosPorStatus["PENDENTE"] ?: emptyList()

                // --- ADICIONE ESTE NOVO BLOCO DE LOGS ---
                Log.d("DebugParticipantes", "---------------------------------")
                Log.d("DebugParticipantes", "Dados recebidos do Firestore:")
                Log.d("DebugParticipantes", "Total de convites encontrados: ${todosOsConvites.size}")
                Log.d("DebugParticipantes", "Número de convites PENDENTES: ${listaDePendentes.size}")
                Log.d("DebugParticipantes", "---------------------------------")
                // --- FIM DO NOVO BLOCO ---

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        confirmados = agrupadosPorStatus["ACEITO"] ?: emptyList(),
                        pendentes = listaDePendentes,
                        recusados = agrupadosPorStatus["RECUSADO"] ?: emptyList()
                    )
                }
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