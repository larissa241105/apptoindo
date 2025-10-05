package com.example.toindoapp.viewmodel.eventos

import Evento
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Definição das abas
enum class EventosTab(val title: String) {
    EXPLORAR("Explorar"),
    MEUS_EVENTOS("Meus Eventos")
}

// Definição do estado da UI
data class EventosUiState(
    val isLoading: Boolean = true,
    val eventos: List<Evento> = emptyList(),
    val selectedTab: EventosTab = EventosTab.EXPLORAR
)

class EventosViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchEventos()
    }

    private fun fetchEventos() {
        viewModelScope.launch {
            // 1. Inicia o carregamento
            _uiState.update { it.copy(isLoading = true) }

            // 2. Simula uma chamada de rede (ex: buscar do Firebase)
            delay(1500) // Atraso de 1.5 segundos

            // 3. Atualiza o estado com os dados e finaliza o carregamento
            _uiState.update {
                it.copy(
                    isLoading = false,
                    eventos = getEventosDeTeste() // Usa a lista de exemplo
                )
            }
        }
    }

    fun onTabSelected(tab: EventosTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        // Aqui você pode adicionar lógica para buscar eventos diferentes para cada aba
    }

    private fun getEventosDeTeste(): List<Evento> {
        return listOf(
            Evento(
                id = "3",
                nome = "Festival de Verão de Manaus",
                data = "04/10/2025",
                horario = "19:00",
                local = "Praia da Ponta Negra",
                categoria = "Música",
                imagemUrl = "https://i.ibb.co/6yF6Nqg/pexels-artem-beliaikin-1831724.jpg"
            ),
            Evento(
                id = "4",
                nome = "Corrida da Meia Noite",
                data = "31/12/2025",
                horario = "22:00",
                local = "Av. das Torres",
                categoria = "Esporte",
                imagemUrl = "https://i.ibb.co/2gchj7b/pexels-pixabay-248547.jpg"
            )
        )
    }
}