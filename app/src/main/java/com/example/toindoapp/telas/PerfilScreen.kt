package com.example.toindoapp.telas // Ajuste o pacote se necessário

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter // <<< 1. IMPORTE AQUI
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
        containerColor = Color(0xFFFffff),
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFF) // Defina a cor de fundo aqui
                )
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
                Box(
                    // Este modifier cria o círculo de fundo cinza
                    modifier = Modifier
                        .size(72.dp) // O tamanho total do círculo
                        .background(
                            color = Color.LightGray, // A cor de fundo que você quer
                            shape = CircleShape      // A forma do fundo
                        ),
                    contentAlignment = Alignment.Center // Centraliza o ícone dentro do Box
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Ícone do Perfil",
                        // O ícone é um pouco menor que o Box para criar a margem
                        modifier = Modifier.size(48.dp),
                        tint = Color.White // Cor do ícone para contrastar com o fundo
                    )
                }

                Text(
                    text = uiState.nome,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color =MaterialTheme.colorScheme.onSurfaceVariant
                )


                Text(
                    text = uiState.email,
                    fontSize = 16.sp,
                    color =MaterialTheme.colorScheme.onSurfaceVariant
                )

                //ADICIONADO: mostrar e copiar uid

                Text(
                    text = uiState.userUid,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                CopyButtonExample(textToCopy = uiState.userUid )


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

//butao para copiar uid
@Composable
private fun CopyButtonExample(textToCopy: String) {
    val clipboardManager = LocalClipboardManager.current

    Column {
        Button(
            onClick = {
                clipboardManager.setText(AnnotatedString(textToCopy))
            }
        ) {
            Text("Clique aqui para copiar seu UID")
        }
    }
}