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
        
        fetchEventosForSelectedTab()
    }

    private fun fetchEventosForSelectedTab() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, eventos = emptyList()) }

            try {
                val currentTab = _uiState.value.selectedTab
                val userId = Firebase.auth.currentUser?.uid
                val firestore = Firebase.firestore.collection("eventos")


                val finalQuery = when (currentTab) {
                    EventosTab.EXPLORAR -> {
                        if (userId != null) {

                            firestore.whereNotEqualTo("creatorId", userId)
                        } else {

                            firestore
                        }
                    }
                    EventosTab.MEUS_EVENTOS -> {

                        if (userId != null) {
                            firestore.whereEqualTo("creatorId", userId)
                        } else {

                            null
                        }
                    }
                }

                val eventos = if (finalQuery != null) {
                    val snapshot = finalQuery.get().await()
                    snapshot.documents.mapNotNull { document ->

                        val eventoObj = document.toObject(Evento::class.java)


                        val isGratuitoFromFirebase = document.getBoolean("isGratuito") ?: false
                        val isPublicoFromFirebase = document.getBoolean("isPublico") ?: true


                        eventoObj?.copy(
                            id = document.id,
                            isGratuito = isGratuitoFromFirebase,
                            isPublico = isPublicoFromFirebase
                        )
                    }
                } else {
                    emptyList()
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

        _uiState.update { it.copy(selectedTab = tab) }
        fetchEventosForSelectedTab()
    }
}