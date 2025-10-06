package com.example.toindoapp.telas

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ConvitesScreen(navController: NavController) {
    Scaffold(
        containerColor = Color(0xFFFfffffff),
        bottomBar = { BottomMenu(navController = navController) }
    ) { innerPadding ->
        Text(
            text = "",
            modifier = Modifier.padding(innerPadding)
        )
    }
}