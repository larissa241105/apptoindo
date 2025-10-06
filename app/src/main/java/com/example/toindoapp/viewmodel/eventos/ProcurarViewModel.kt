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

    // StateFlow interno para a lista de eventos brutos (sem filtros)
    private val _eventosBrutos = MutableStateFlow<List<Evento>>(emptyList())

    init {
        fetchEventos()

        // Combina os fluxos de dados para aplicar os filtros de forma reativa
        viewModelScope.launch {
            combine(
                _eventosBrutos,
                _uiState.asStateFlow()
            ) { eventosBrutos, uiState ->
                // Aplica a lógica de filtragem
                val filteredEventos = eventosBrutos.filter { evento ->
                    // Filtro de pesquisa (se o nome do evento contém o texto)
                    val matchesSearch = evento.nome.contains(uiState.searchText, ignoreCase = true)

                    // Filtro de categoria
                    val matchesCategory = if (uiState.selectedCategories.contains(EventoCategoria.TODOS)) {
                        true // Se 'TODOS' estiver selecionado, não filtra por categoria
                    } else {
                        // Verifica se a categoria do evento está no conjunto de categorias selecionadas
                        uiState.selectedCategories.contains(EventoCategoria.valueOf(evento.categoria.uppercase()))
                    }
                    matchesSearch && matchesCategory
                }
                uiState.copy(eventos = filteredEventos, isLoading = false)
            }.collect { updatedState ->
                _uiState.value = updatedState
            }
        }
    }

    private fun fetchEventos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Pega todos os eventos, exceto os criados pelo usuário logado (lógica do "Explorar")
                val userId = Firebase.auth.currentUser?.uid
                val firestore = Firebase.firestore.collection("eventos")

                val query = if (userId != null) {
                    firestore.whereNotEqualTo("creatorId", userId)
                } else {
                    firestore
                }

                val snapshot = query.get().await()
                val eventos = snapshot.documents.mapNotNull { document ->
                    document.toObject(Evento::class.java)?.copy(id = document.id)
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

    fun onCategorySelected(category: EventoCategoria) {
        _uiState.update { currentState ->
            val newCategories = when {
                // Se a categoria 'TODOS' for selecionada, deseleciona todas as outras
                category == EventoCategoria.TODOS -> {
                    setOf(EventoCategoria.TODOS)
                }
                // Se já estiver selecionada e houver outras, deseleciona
                currentState.selectedCategories.contains(category) -> {
                    currentState.selectedCategories - category
                }
                // Se não estiver selecionada, a adiciona
                else -> {
                    // Deseleciona 'TODOS' se outra categoria for selecionada
                    (currentState.selectedCategories - EventoCategoria.TODOS) + category
                }
            }
            // Garante que, se nenhuma for selecionada, 'TODOS' volte a ser o padrão
            currentState.copy(selectedCategories = if (newCategories.isEmpty()) setOf(EventoCategoria.TODOS) else newCategories)
        }
    }
}