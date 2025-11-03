package com.example.toindoapp.telas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.toindoapp.viewmodel.eventos.ProcurarViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcurarScreen(
    navController: NavController,
    vm: ProcurarViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    val localizacaoInicial = LatLng(-3.1190, -60.0217) // Manaus
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(localizacaoInicial, 12f)
    }

    LaunchedEffect(uiState.eventos) {
        if (uiState.eventos.isNotEmpty()) {
            println("DEBUG: Eventos carregados: ${uiState.eventos.size}")
            println("DEBUG: Coordenadas do primeiro evento: (${uiState.eventos.first().latitude}, ${uiState.eventos.first().longitude})")
        } else {
            println("DEBUG: uiState.eventos está vazio.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procurar") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFFF)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {

            BottomMenu(navController = navController)
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                uiState.eventos.forEach { evento ->

                    val posicao = LatLng(evento.latitude, evento.longitude)
                    Marker(
                        state = MarkerState(position = posicao),
                        title = evento.nome,
                        snippet = evento.local
                    )
                }
            }

                OutlinedTextField(
                    value = uiState.searchText,
                    onValueChange = { vm.onSearchTextChange(it) },
                    placeholder = { Text("Pesquisar eventos...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",

                            tint = Color(0xFFDF4A1B)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        // 2. Cor da linha/borda alterada aqui
                        focusedBorderColor = Color(0xFFDF4A1B),
                        unfocusedBorderColor = Color(0xFFDF4A1B)
                    )
                )
            }
        }
    }
