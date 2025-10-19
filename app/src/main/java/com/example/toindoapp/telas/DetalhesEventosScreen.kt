package com.example.toindoapp.telas

import android.R.attr.fontWeight
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesEventoScreen(
    navController: NavController,
    eventoId: String,
    vm: DetalhesEventoViewModel = viewModel(factory = DetalhesEventoViewModel.Factory(eventoId))
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current
    var convidado by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val conviteStatus by vm.conviteStatus.collectAsState()

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

    LaunchedEffect(conviteStatus) {
        conviteStatus?.let { status ->
            scope.launch {
                snackbarHostState.showSnackbar(status)
                vm.clearConviteStatus() // Limpa o status para não mostrar novamente
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            // Uma variável de estado para controlar se o menu está aberto ou fechado
            var menuAberto by remember { mutableStateOf(false) }

            TopAppBar(
                title = { Text(uiState.evento?.nome ?: "Detalhes do Evento") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFFF) // Corrigido para 8 Fs (FF FFFFFF)
                ),

                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                // O slot 'actions' é para ícones e menus no canto direito
                actions = {
                    // Box é usado para ancorar o DropdownMenu ao IconButton
                    Box {
                        // Ícone de três pontinhos que abre o menu ao ser clicado
                        IconButton(onClick = { menuAberto = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                        }

                        // O menu que aparece quando 'menuAberto' é true
                        DropdownMenu(
                            expanded = menuAberto,
                            onDismissRequest = { menuAberto = false }, // Fecha o menu se o usuário clicar fora
                            modifier = Modifier.width(220.dp)
                        ) {
                            // Item do menu "Participantes"
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Participantes",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                },
                                onClick = {
                                    menuAberto = false
                                    val eventoId = uiState.evento?.id
                                    val creatorId = uiState.evento?.creatorId // Pega o ID do criador do evento

                                    // Garante que ambos os IDs não são nulos antes de navegar
                                    if (eventoId != null && creatorId != null) {
                                        navController.navigate("participantes_screen/$eventoId/$creatorId")
                                    }
                                },
                                        colors = MenuDefaults.itemColors(
                                leadingIconColor = Color(0xFFFFFFFF) // Define a cor do ícone (se houver)
                            )
                            )
                        }
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
                        Spacer(modifier = Modifier.height(16.dp))
                        // Botões Exclusivos para o Criador
                        if (uiState.isUserCreator) {

                            // Toda a sua coluna de controles do criador vai aqui dentro
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    "Convidar um amigo:",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                OutlinedTextField(
                                    value = convidado,
                                    onValueChange = { convidado = it },
                                    label = { Text("Insira o UID do seu amigo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    enabled = !uiState.isSendingInvite
                                )
                                Spacer(modifier = Modifier.height(20.dp)) // Adicionei um Spacer para separar os botões

                                Button(
                                    // Corrigido: Removido o .fillMaxWidth() duplicado
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        vm.enviarConvite(convidado)
                                        convidado = ""
                                    },
                                    enabled = !uiState.isSendingInvite
                                ) {
                                    if (uiState.isSendingInvite) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = "Enviar Convite",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Botão de Excluir
                                Button(
                                    onClick = { vm.deleteEvento() },
                                    // Corrigido: Removido o .fillMaxWidth() duplicado
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Excluir Evento",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        }}}
            }
        }
    }
