package com.example.toindoapp.telas

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import com.example.toindoapp.data.eventos.Convite
import com.example.toindoapp.viewmodel.eventos.ParticipantesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ParticipantesScreen(navController: NavController, eventoId: String, creatorId: String) {

    // Instancia a ViewModel usando a Factory para passar o eventoId
    val vm: ParticipantesViewModel = viewModel(factory = ParticipantesViewModel.Factory(eventoId, creatorId))
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFFFFFF),
        topBar = {
            TopAppBar(
                title = { Text("Participantes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFF) // Defina a cor de fundo aqui
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
                items(uiState.confirmados) { convite ->
                    ParticipanteItem(convite = convite)
                }


                if (uiState.isUserCreator) {
                    stickyHeader {
                        HeaderDaSecao(texto = "Pendentes ⏳",
                            backgroundColor = Color(0xFFFFFFFF)
                            )
                    }
                    items(uiState.pendentes) { convite ->
                        ParticipanteItem(convite = convite)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderDaSecao(texto: String, backgroundColor: Color = MaterialTheme.colorScheme.background) {
    Surface(
        color = backgroundColor, // Usa a cor que você passar
        tonalElevation = 0.dp, // <- A MÁGICA ACONTECE AQUI: desativa o "tint" rosado
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
fun ParticipanteItem(convite: Convite) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // NOTA: O ideal é exibir o NOME do usuário, e não o UID.
            // Veja a sugestão no final da resposta.
            Text(
                text = convite.convidadoUid, // Temporário: exibir o UID
                fontSize = 16.sp
            )
        }
    }
}