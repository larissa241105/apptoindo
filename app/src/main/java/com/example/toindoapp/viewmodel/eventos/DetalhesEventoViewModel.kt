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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Adicione um estado de ação para a UI saber o que fazer após a exclusão
enum class EventoActionState { IDLE, DELETED }

data class DetalhesEventoUiState(
    val isLoading: Boolean = true,
    val evento: Evento? = null,
    val isUserCreator: Boolean = false,
    val error: String? = null,
    val actionState: EventoActionState = EventoActionState.IDLE, // Novo estado de ação
    //val participants: List<String> = listOf()
)

class DetalhesEventoViewModel(private val eventoId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalhesEventoUiState())
    val uiState = _uiState.asStateFlow()

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
                    it.copy(
                        isLoading = false,
                        evento = evento,
                        isUserCreator = isCreator
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // --- Nova lógica de exclusão ---
    fun deleteEvento() {
        viewModelScope.launch {
            try {
                // Remove o documento do Firestore
                Firebase.firestore.collection("eventos")
                    .document(eventoId)
                    .delete()
                    .await()

                // Atualiza o estado para informar a UI que a exclusão foi bem-sucedida
                _uiState.update { it.copy(actionState = EventoActionState.DELETED) }
            } catch (e: Exception) {
                // Lida com erros
                _uiState.update { it.copy(error = "Falha ao excluir o evento: ${e.message}") }
            }
        }
    }


    // --- Lógica de Compartilhamento (exemplo simples) ---
    fun getShareableLink(): String {
        // Isso é um placeholder. A URL real dependeria de um link dinâmico do Firebase.
        return "https://site.com/$eventoId"
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

/*
    fun adicionaParticipante(convidado:String){

        val participants = Evento(participants = listOf(convidado)

        viewModelScope.launch {
            try {
                Firebase.firestore.collection("eventos")
                    .document(eventoId)
                    .update("participants",participants)

            } catch (e: Exception) {
            }
        }
    }
*/

}