package com.example.toindoapp.telas

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.toindoapp.navigation.Screen
import com.example.toindoapp.R
// A data class não muda
data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
)

@Composable
fun BottomMenu(navController: NavController) {
    val navItems = listOf(
        BottomNavItem("Evento", Screen.Eventos.route, R.drawable.ic_celebration_filled, R.drawable.ic_celebration_outlined),
        BottomNavItem("Procurar", Screen.Procurar.route, R.drawable.ic_search_filled, R.drawable.ic_search_outlined),
        BottomNavItem("Convites", Screen.Convites.route, R.drawable.ic_mail_filled, R.drawable.ic_mail_outlined),
        BottomNavItem("Perfil", Screen.Perfil.route, R.drawable.ic_person_filled, R.drawable.ic_person_outlined)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(

        containerColor = MaterialTheme.colorScheme.surface,

        tonalElevation = 1.dp
    ) {
        navItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(text = item.label) },
                icon = {
                    Icon(
                        painter = painterResource(id = if (isSelected) item.selectedIcon else item.unselectedIcon),
                        contentDescription = item.label
                    )
                },
                // ### MUDANÇA 2: Cores dos Ícones e Textos ###
                // Também trocamos as cores fixas por cores do tema para garantir
                // que o texto e os ícones fiquem legíveis em ambos os modos.
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}