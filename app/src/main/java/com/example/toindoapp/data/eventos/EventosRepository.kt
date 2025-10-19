package com.example.toindoapp.data.eventos

import Evento

class EventosRepository {

    fun getMeusEventos(): List<Evento> {
        // Lista de exemplo para "Meus Eventos"
        return listOf(
            Evento(
                "1",
                "Show de Rock",
                "15/10/2024",
                "Arena da Amazônia",
                "https://exemplo.com/show_rock.jpg"
            ),
            Evento(
                "2",
                "Festival de Cinema",
                "22/11/2024",
                "Cinepolis",
                "https://exemplo.com/festival_cinema.jpg"
            )
        )
    }

    fun getEventosCriados(): List<Evento> {
        // Lista de exemplo para "Eventos Criados por Mim"
        return listOf(
            Evento(
                "3",
                "Feira Gastronômica",
                "05/12/2024",
                "Centro de Convenções",
                "https://exemplo.com/feira_gastronomica.jpg"
            ),
            Evento(
                "4",
                "Maratona Anual",
                "18/01/2025",
                "Ponta Negra",
                "https://exemplo.com/maratona.jpg"
            )
        )
    }
}