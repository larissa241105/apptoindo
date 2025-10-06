package com.example.toindoapp.telas // Ajuste o pacote se necessário

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter // <<< 1. IMPORTE AQUI
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.toindoapp.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.toindoapp.navigation.Screen // Importe sua classe Screen
import com.example.toindoapp.viewmodel.perfil.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    vm: PerfilViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val logoutSuccess by vm.logoutSuccess.collectAsState()

    LaunchedEffect(logoutSuccess) {
        if (logoutSuccess) {
            navController.navigate(Screen.SignIn.route) {
                popUpTo(0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // <<< 2. CRIE A VARIÁVEL DO PAINTER AQUI
                val placeholderPainter = rememberVectorPainter(image = Icons.Default.AccountCircle)

                // Foto do Perfil
                GlideImage(
                    model = uiState.fotoUrl,
                    contentDescription = "Foto do Perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                ) {
                    // <<< 3. USE A VARIÁVEL AQUI
                    it.fallback(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .placeholder(R.drawable.placeholder_image) // Boa prática adicionar placeholder também
                }

                Text(
                    text = uiState.nome,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = uiState.email,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { vm.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("SAIR (LOGOUT)")
                }
            }
        }
    }
}