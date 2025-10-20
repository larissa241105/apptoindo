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
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcurarScreen(
    navController: NavController,
    vm: ProcurarViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    val localizacaoInicial = LatLng(-3.1190, -60.0217)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(localizacaoInicial, 12f)
    }

    // O Scaffold volta a ser o componente principal para organizar a tela
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procurar") }, // Alterei o título para "Procurar"
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFFF) // Cor de fundo branca
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            // Supondo que você tenha um Composable chamado BottomMenu
            BottomMenu(navController = navController)
        }
    ) { innerPadding ->
        // O Box agora fica dentro do Scaffold e usa o innerPadding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplica o padding para não sobrepor as barras
        ) {
            // 1. O Mapa ocupa todo o espaço no fundo
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Conteúdo do mapa vazio
            }

            // 2. A Barra de Pesquisa fica sobre o mapa
            OutlinedTextField(
                value = uiState.searchText,
                onValueChange = { vm.onSearchTextChange(it) },
                placeholder = { Text("Pesquisar eventos...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar",
                        // 1. Cor do ícone alterada aqui
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
                    unfocusedBorderColor = Color(0xFFDF4A1B) // Opcional: mesma cor para a borda não focada
                )
            )
        }
    }
}

// **Lembre-se de ter o seu Composable BottomMenu definido em algum lugar, por exemplo:**