package com.example.toindoapp.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.toindoapp.viewmodel.eventos.ParticipanteDisplay
import com.example.toindoapp.viewmodel.eventos.ParticipantesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantesScreen(navController: NavController, eventoId: String, creatorId: String) {

    val vm: ParticipantesViewModel = viewModel(factory = ParticipantesViewModel.Factory(eventoId, creatorId))
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Participantes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, 
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFFFFFFF)
    ) { innerPadding ->
        if (uiState.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {

                stickyHeader {
                    HeaderDaSecao(texto = "Confirmados ✅")
                }
                items(uiState.confirmados, key = { it.userId }) { participante ->
                    ParticipanteItem(
                        participante = participante,
                        isConfirmed = true
                    )
                }

                if (uiState.isUserCreator) {
                    stickyHeader {
                        HeaderDaSecao(texto = "Pendentes ⏳")
                    }
                    items(uiState.pendentes, key = { it.userId }) { participante ->
                        ParticipanteItem(
                            participante = participante,
                            isConfirmed = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderDaSecao(texto: String) {
    Surface(
        color = Color(0xFFFFE0B2), // laranja clarinho
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}

@Composable
fun ParticipanteItem(participante: ParticipanteDisplay, isConfirmed: Boolean) {
    val backgroundColor = Color.White
    val textColor = Color.Black

    Surface(
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = participante.nome,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

