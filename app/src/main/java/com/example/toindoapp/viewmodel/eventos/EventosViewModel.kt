package com.example.toindoapp.viewmodel.eventos

import Evento
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class EventosTab(val title: String) {
    EXPLORAR("Explorar"),
    MEUS_EVENTOS("Meus Eventos")
}

data class EventosUiState(
    val isLoading: Boolean = true,
    val eventos: List<Evento> = emptyList(),
    val selectedTab: EventosTab = EventosTab.EXPLORAR,
    val error: String? = null
)

class EventosViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Ao iniciar, busca os eventos da primeira aba (Explorar)
        fetchEventosForSelectedTab()
    }

    private fun fetchEventosForSelectedTab() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, eventos = emptyList()) }

            try {
                val currentTab = _uiState.value.selectedTab
                val userId = Firebase.auth.currentUser?.uid
                val firestore = Firebase.firestore.collection("eventos")

                // Cria a query base
                val finalQuery = when (currentTab) {
                    EventosTab.EXPLORAR -> {
                        if (userId != null) {
                            // Busca todos os eventos criados por outras pessoas
                            firestore.whereNotEqualTo("creatorId", userId)
                        } else {
                            // Se não houver usuário logado, mostra todos os eventos
                            firestore
                        }
                    }
                    EventosTab.MEUS_EVENTOS -> {
                        // Busca apenas os eventos criados pelo usuário logado
                        if (userId != null) {
                            firestore.whereEqualTo("creatorId", userId)
                        } else {
                            // Se o usuário não estiver logado, não busca nada
                            null
                        }
                    }
                }

                val eventos = if (finalQuery != null) {
                    val snapshot = finalQuery.get().await()
                    snapshot.documents.mapNotNull { document ->
                        // 1. Mapeia o documento para o objeto Evento
                        val eventoObj = document.toObject(Evento::class.java)

                        // 2. Lê explicitamente o campo 'isGratuito' do documento
                        val isGratuitoFromFirebase = document.getBoolean("isGratuito") ?: false

                        // 3. Retorna uma cópia do objeto com o valor corrigido
                        eventoObj?.copy(
                            id = document.id,
                            isGratuito = isGratuitoFromFirebase // Usa o valor lido explicitamente
                        )
                    }
                } else {
                    emptyList() // Retorna uma lista vazia se a query for nula
                }

                _uiState.update {
                    it.copy(isLoading = false, eventos = eventos)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Falha ao buscar eventos: ${e.message}")
                }
            }
        }
    }


    fun onTabSelected(tab: EventosTab) {
        // Atualiza a aba selecionada e busca os eventos correspondentes
        _uiState.update { it.copy(selectedTab = tab) }
        fetchEventosForSelectedTab()
    }
}