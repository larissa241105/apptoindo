package com.example.toindoapp.telas

import android.R.attr.fontWeight
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
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


    LaunchedEffect(uiState.actionState) {
        when (uiState.actionState) {
            EventoActionState.DELETED -> {
                Toast.makeText(context, "Evento excluído com sucesso!", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Volta para a tela anterior
            }

            else -> {}
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
            var menuAberto by remember { mutableStateOf(false) }

            TopAppBar(
                title = { Text(uiState.evento?.nome ?: "Detalhes do Evento") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                ),

                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuAberto = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                        }
                        DropdownMenu(
                            expanded = menuAberto,
                            onDismissRequest = { menuAberto = false },
                            modifier = Modifier.width(220.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Participantes", fontWeight = FontWeight.Medium, fontSize = 16.sp) },
                                onClick = {
                                    menuAberto = false
                                    val eventoId = uiState.evento?.id
                                    val creatorId = uiState.evento?.creatorId
                                    if (eventoId != null && creatorId != null) {
                                        navController.navigate("participantes_screen/$eventoId/$creatorId")
                                    }
                                }
                            )
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        when (uiState.isLoading) {
            true -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            false -> {
                uiState.evento?.let { evento ->

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            AsyncImage(
                                model = evento.imagemUrl,
                                contentDescription = evento.nome,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.churrasco),
                                error = painterResource(id = R.drawable.churrasco)
                            )
                            Spacer(modifier = Modifier.height(24.dp))


                            Text(
                                text = evento.nome,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 40.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                DetalheInfoCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    icon = Icons.Filled.CalendarToday,
                                    titulo = "Data e Horário",
                                    valor = "${evento.data} - ${evento.horario}"
                                )


                                DetalheInfoCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    icon = Icons.Filled.LocationOn,
                                    titulo = "Localização",
                                    valor = evento.local
                                )


                                val custoValor: String = try {

                                    val precoComoString = evento.preco?.toString()

                                    if (precoComoString.isNullOrBlank()) {
                                        "Gratuito"
                                    } else {
                                        val precoNumerico = precoComoString.replace(",", ".").toDouble()
                                        if (precoNumerico == 0.0) {
                                            "Gratuito"
                                        } else {

                                            "R$ $precoComoString"
                                        }
                                    }
                                } catch (e: Exception) {

                                    evento.preco?.toString() ?: "Inválido"
                                }


                                DetalheInfoCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    icon = Icons.Filled.ConfirmationNumber,
                                    titulo = "Custo",
                                    valor = custoValor
                                )

                            }
                            Spacer(modifier = Modifier.height(32.dp))


                            SobreOEventoSection(
                                descricao = evento.nome,
                                categoria = evento.categoria
                            )

                            Spacer(modifier = Modifier.height(32.dp))


                            if (uiState.isUserCreator) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
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
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        modifier = Modifier.fillMaxWidth().height(52.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        onClick = {
                                            vm.enviarConvite(convidado)
                                            convidado = ""
                                        },
                                        enabled = !uiState.isSendingInvite
                                    ) {
                                        if (uiState.isSendingInvite) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                        } else {
                                            Text("Enviar Convite", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                    Button(
                                        onClick = { vm.deleteEvento() },
                                        modifier = Modifier.fillMaxWidth().height(52.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Excluir Evento", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp)) // Espaço no final da rolagem
                        }
                    }
                }
            }
        }
    }
}



//adicionando os cards de design
@Composable
fun DetalheInfoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    titulo: String,
    valor: String
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = titulo,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )


            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                maxLines = 2,
                minLines = 2
            )
        }
    }
}

// o clip das categorias do evento
@Composable
fun CategoryChip(
    category: String
) {
    Surface(
        modifier = Modifier.padding(end = 10.dp, bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.tertiary
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
    }
}


@Composable
fun SobreOEventoSection(
    descricao: String,
    categoria: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 35.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Sobre o evento",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (categoria.isNotBlank()) {
            CategoryChip(category = categoria)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (descricao.isNotBlank()) {
            Text(
                text = descricao,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}