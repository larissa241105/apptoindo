package com.example.toindoapp.navigation

sealed class Screen(val route:String) {

    data object Splash: Screen("splash")
    data object LoginOptions: Screen("login_options")
    data object SignIn: Screen("sign_in")
    data object SignUp: Screen("sign_up")
    data object Profile : Screen("profile")
    data object Eventos : Screen("eventos")
    data object Procurar : Screen("procurar")
    data object Convites : Screen("convites")
    data object CadastroEvento : Screen("cadastro_evento")

}