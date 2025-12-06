package com.example.toindoapp.viewmodel.eventos

import Evento
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Enum para as categorias de eventos
enum class EventoCategoria(val title: String) {
    TODOS("Todos"),
    SHOWS("Shows"),
    ESPORTES("Esportes"),
    WORKSHOPS("Workshops"),
    OUTROS("Outros")
}

data class ProcurarUiState(
    val isLoading: Boolean = true,
    val eventos: List<Evento> = emptyList(),
    val searchText: String = "",
    val selectedCategories: Set<EventoCategoria> = setOf(EventoCategoria.TODOS),
    val error: String? = null
)

class ProcurarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProcurarUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventosBrutos = MutableStateFlow<List<Evento>>(emptyList())

    init {
        fetchEventos()


        viewModelScope.launch {
            combine(
                _eventosBrutos,
                _uiState.asStateFlow().map { it.searchText }.distinctUntilChanged(),
                _uiState.asStateFlow().map { it.selectedCategories }.distinctUntilChanged()
            ) { eventosBrutos, searchText, selectedCategories ->

                val filteredEventos = eventosBrutos.filter { evento ->

                    val matchesSearch = evento.nome.contains(searchText, ignoreCase = true)


                    val matchesCategory = if (selectedCategories.contains(EventoCategoria.TODOS)) {
                        true
                    } else {
                        try {

                            val categoriaDoEvento = EventoCategoria.valueOf(evento.categoria.uppercase())
                            selectedCategories.contains(categoriaDoEvento)
                        } catch (e: Exception) {
                            false
                        }
                    }
                    matchesSearch && matchesCategory
                }

                filteredEventos

            }.collect { filteredEventos ->

                _uiState.update {
                    it.copy(
                        eventos = filteredEventos,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun fetchEventos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = Firebase.auth.currentUser?.uid
                val firestore = Firebase.firestore.collection("eventos")

                val query = if (userId != null) {
                    firestore.whereNotEqualTo("creatorId", userId)
                } else {
                    firestore
                }

                val snapshot = query.get().await()
                val eventos = snapshot.documents.mapNotNull { document ->

                    val eventoObj = document.toObject(Evento::class.java)

                    val lat = document.getDouble("latitude") ?: 0.0
                    val lng = document.getDouble("longitude") ?: 0.0
                    val publico = document.getBoolean("publico") ?: true
                    val isGratuito = document.getBoolean("isGratuito") ?: false
                    val contagem = document.getLong("participantesCount")?.toInt() ?: 0


                    eventoObj?.copy(
                        id = document.id,
                        latitude = lat,
                        longitude = lng,
                        publico = publico,
                        isGratuito = isGratuito,
                        participantesCount = contagem
                    )
                }

                _eventosBrutos.value = eventos

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Falha ao buscar eventos: ${e.message}")
                }
            }
        }
    }


    fun onSearchTextChange(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }

}