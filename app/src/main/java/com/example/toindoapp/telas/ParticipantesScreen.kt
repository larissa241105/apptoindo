package com.example.toindoapp.telas

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.toindoapp.viewmodel.eventos.ParticipanteDisplay
import com.example.toindoapp.viewmodel.eventos.ParticipantesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ParticipantesScreen(navController: NavController, eventoId: String, creatorId: String) {


    val vm: ParticipantesViewModel = viewModel(factory = ParticipantesViewModel.Factory(eventoId, creatorId))
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFFFFFF),
        topBar = {
            TopAppBar(
                title = { Text("Participantes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFF)
                ),
                        navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),



            ) {
                // Seção de Confirmados (visível para todos)
                stickyHeader {
                    HeaderDaSecao(texto = "Confirmados ✅",
                        backgroundColor = Color(0xFFFFFFFF)
                        )
                }
                items(uiState.confirmados, key = { it.userId }) { participante ->

                    ParticipanteItem(participante = participante)
                }


                if (uiState.isUserCreator) {
                    stickyHeader {
                        HeaderDaSecao(texto = "Pendentes ⏳",
                            backgroundColor = Color(0xFFFFFFFF)
                            )
                    }
                    items(uiState.pendentes, key = { it.userId }) { participante ->

                        ParticipanteItem(participante = participante)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderDaSecao(texto: String, backgroundColor: Color = MaterialTheme.colorScheme.background) {
    Surface(
        color = backgroundColor,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
    Text(
        text = texto,

        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    )
}}

@Composable
fun ParticipanteItem(participante: ParticipanteDisplay) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = participante.nome,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

    }
}