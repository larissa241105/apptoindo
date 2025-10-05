package com.example.toindoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.toindoapp.navigation.AppNav
import com.example.toindoapp.ui.theme.ToindoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Permite que o app desenhe atrás das barras do sistema (status e navegação)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ToindoTheme {
                AppNav()
            }
        }
    }
}