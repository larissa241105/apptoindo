package com.example.toindoapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ... (Seus esquemas de cores)
private val DarkColorScheme = darkColorScheme(
    primary = roxo,
    secondary = cinzaclaro,
    tertiary = laranja
)

private val LightColorScheme = lightColorScheme(
    primary = roxo,
    secondary = cinzaclaro,
    tertiary = laranja,

)


@Composable
fun ToindoTheme(
    // Defina darkTheme diretamente como false para forçar o tema claro
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Note que a variável 'darkTheme' acima já foi definida como 'false'
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Ajuste para deixar a barra de status transparente
            window.statusBarColor = Color.Transparent.toArgb()
            // Garante que os ícones da barra (hora, bateria) fiquem ESCUROS no tema claro
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}