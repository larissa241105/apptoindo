package com.example.toindoapp.telas

import Evento
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.toindoapp.navigation.Screen
import com.example.toindoapp.viewmodel.eventos.EventosTab
import com.example.toindoapp.viewmodel.eventos.EventosViewModel
import com.example.toindoapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(
    navController: NavController,
    vm: EventosViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFFFFFF),
        topBar = {
            TopAppBar(
                title = { Text("Eventos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFF) // Defina a cor de fundo aqui
                )
            )
        },
        bottomBar = { BottomMenu(navController = navController) },

        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.CadastroEvento.route) },
                    containerColor = Color(0xfffDF4A1B)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Evento",
                    tint = Color.White
                )
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = Color(0xFFFFFFF)) {
                EventosTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { vm.onTabSelected(tab) },
                        text = { Text(tab.title) },

                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.eventos.isEmpty()) {
                    Text("Nenhum evento encontrado.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(uiState.eventos) { evento ->
                            EventoCard(
                                evento = evento,
                                onClick = {
                                    // Esta linha é a responsável por iniciar a navegação
                                    navController.navigate("detalhes_evento/${evento.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EventoCard(evento: Evento, onClick: () -> Unit) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        // Altere a cor de fundo do Card aqui
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F0F0) // Exemplo: um cinza claro
        )
    ) {

        Column {

            AsyncImage(
                model = evento.imagemUrl,
                contentDescription = evento.nome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop,
                // O placeholder e o erro agora funcionam perfeitamente com o Coil
                placeholder = painterResource(id = R.drawable.churrasco),
                error = painterResource(id = R.drawable.churrasco)
            )


            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = evento.nome,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.DateRange, contentDescription = "Data", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${evento.data} às ${evento.horario}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "Local", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = evento.local, style = MaterialTheme.typography.bodyMedium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                // --- LINHAS ADICIONADAS PARA MOSTRAR O PREÇO ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.AttachMoney, contentDescription = "Preço", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(8.dp))
                    val precoTexto = if (evento.isGratuito || evento.preco == 0.0) {
                        "Gratuito"
                    } else {

                        String.format("R$ %.2f", evento.preco).replace('.', ',')
                    }
                    Text(text = precoTexto, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                }
            }
        }
    }
}