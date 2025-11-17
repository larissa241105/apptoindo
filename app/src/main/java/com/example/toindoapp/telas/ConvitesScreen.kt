package com.example.toindoapp.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.toindoapp.data.eventos.Convite
import com.example.toindoapp.viewmodel.eventos.ConvitesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvitesScreen(
    navController: NavController,
    vm: ConvitesViewModel = viewModel()
) {

    val listaDeConvites = vm.convites.value

    Scaffold(
        containerColor = Color(0xFFFFFFFF),
        topBar = {
            TopAppBar(
                title = { Text("Meus Convites") },
                    colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFFF)
                    )
            )


        },
        bottomBar = { BottomMenu(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (listaDeConvites.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Você não tem novos convites.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaDeConvites) { convite ->
                        ItemConvite(
                            convite = convite,
                            onAceitarClick = { vm.aceitarConvite(convite.id) },
                            onRecusarClick = { vm.recusarConvite(convite.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemConvite(
    convite: Convite,
    onAceitarClick: () -> Unit,
    onRecusarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                containerColor = Color.White
                )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = convite.nomeDoEvento,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Convidado por: ${convite.quemConvidou}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onRecusarClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Recusar")
                }
                Button(onClick = onAceitarClick) {
                    Text("Aceitar")
                }
            }
        }
    }
}