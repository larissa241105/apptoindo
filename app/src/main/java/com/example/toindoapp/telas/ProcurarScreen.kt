package com.example.toindoapp.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.example.toindoapp.viewmodel.eventos.EventoCategoria
import com.example.toindoapp.viewmodel.eventos.ProcurarViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcurarScreen(
    navController: NavController,
    vm: ProcurarViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    // O Scaffold define a estrutura da tela com TopBar, BottomBar e o conteúdo principal.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procurar") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF0F0F0) // Defina a cor de fundo aqui
                )
            )
        },
        bottomBar = { BottomMenu(navController = navController) },
        containerColor = Color(0xFFF0F0F0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Aplica o padding do Scaffold para o conteúdo não ser coberto pelo menu
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Adiciona um Spacer para empurrar a barra de pesquisa para baixo
            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Pesquisa
            OutlinedTextField(
                value = uiState.searchText,
                onValueChange = { vm.onSearchTextChange(it) },
                placeholder = { Text("Pesquisar eventos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Pesquisar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            // Chips de Filtro
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(EventoCategoria.values()) { category ->
                    FilterChip(
                        selected = uiState.selectedCategories.contains(category),
                        onClick = { vm.onCategorySelected(category) },
                        label = { Text(category.title) }
                    )
                }
            }

            // Exibição dos eventos (Carregando, Vazio ou Lista)
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.eventos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum evento encontrado.")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
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