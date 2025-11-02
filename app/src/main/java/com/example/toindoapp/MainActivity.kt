package com.example.toindoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.toindoapp.navigation.AppNav
import com.example.toindoapp.ui.theme.ToindoTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(applicationContext, "AIzaSyBB9e6FcqEH5yXh81dHwyu-rp3xqLafvPU")
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ToindoTheme {
                AppNav()
            }
        }
    }
}