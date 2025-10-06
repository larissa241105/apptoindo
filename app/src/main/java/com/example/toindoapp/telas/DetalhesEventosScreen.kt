package com.example.toindoapp.telas

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.toindoapp.viewmodel.DetalhesEventoViewModel
import com.example.toindoapp.viewmodel.EventoActionState
import coil.compose.AsyncImage
import com.example.toindoapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesEventoScreen(
    navController: NavController,
    eventoId: String,
    vm: DetalhesEventoViewModel = viewModel(factory = DetalhesEventoViewModel.Factory(eventoId))
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    // Observe o estado de ação para navegar de volta após a exclusão
    LaunchedEffect(uiState.actionState) {
        when (uiState.actionState) {
            EventoActionState.DELETED -> {
                Toast.makeText(context, "Evento excluído com sucesso!", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Volta para a tela anterior
            }

            else -> {} // Não faz nada em outros estados
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.evento?.nome ?: "Detalhes do Evento") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFF) // Defina a cor de fundo aqui
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        containerColor = Color(0xFFFFFFF)
    ) { innerPadding ->
        when (uiState.isLoading) {
            true -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            false -> {
                uiState.evento?.let { evento ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = evento.imagemUrl,
                            contentDescription = evento.nome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.churrasco),
                            error = painterResource(id = R.drawable.churrasco)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = evento.nome,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Data: ${evento.data} às ${evento.horario}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Local: ${evento.local}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Preço: R$ ${evento.preco}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Botões Exclusivos para o Criador
                        if (uiState.isUserCreator) {
                            Spacer(modifier = Modifier.height(24.dp))
                            // Use Row para alinhar os botões horizontalmente
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Botão de Excluir
                                Button(onClick = { vm.deleteEvento() },  modifier = Modifier.fillMaxWidth()) {
                                    Text("Excluir Evento")

                                }

                                // Botão de Compartilhar
                                Button(onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Confira este evento: ${evento.nome}")
                                        putExtra(Intent.EXTRA_TEXT, "Data: ${evento.data}\nLocal: ${evento.local}\n\n${vm.getShareableLink()}")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
                                },   modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xfffDF4A1B), // Exemplo: um azul
                                        contentColor = Color.White
                                    )) {
                                    Text("Compartilhar Link")
                                }
                            }
                        }else{
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Confira este evento: ${evento.nome}")
                                    putExtra(Intent.EXTRA_TEXT, "Data: ${evento.data}\nLocal: ${evento.local}\n\n${vm.getShareableLink()}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
                            },   modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xfffDF4A1B), // Exemplo: um azul
                                    contentColor = Color.White
                                )) {
                                Text("Compartilhar Link")
                            }
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Evento não encontrado.")
                    }
                }
            }
        }
    }
}