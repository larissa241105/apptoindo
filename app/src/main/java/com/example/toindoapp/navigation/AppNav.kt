package com.example.toindoapp.navigation

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.toindoapp.R
import com.example.toindoapp.auth.AuthViewModel
import com.example.toindoapp.telas.AuthMode
import com.example.toindoapp.telas.ConvitesScreen
import com.example.toindoapp.telas.PerfilScreen
import com.example.toindoapp.telas.SignInScreen
import com.example.toindoapp.telas.SplashScreen
import com.example.toindoapp.telas.EventosScreen // Verifique se o import está correto
import com.example.toindoapp.telas.CadastroEventoScreen
import com.example.toindoapp.telas.DetalhesEventoScreen

import com.example.toindoapp.telas.LoginScreen
import com.example.toindoapp.telas.ProcurarScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun AppNav(vm: AuthViewModel = viewModel()) {
    val nav = rememberNavController()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    // Launcher para o resultado do Login com Google (sem alterações)
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            vm.firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("AppNav", "Google sign in failed", e)
        }
    }

    // Cliente para login e logout do Google (sem alterações)
    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    )

    // Efeito de navegação centralizado: Observa o estado do usuário
    LaunchedEffect(key1 = state.user) {
        if (state.user != null) {
            // Navega o usuário para a tela de Eventos após o login bem-sucedido.
            gotoEventos(nav)
        }
    }

    // Grafo de Navegação completo e organizado
    NavHost(navController = nav, startDestination = Screen.Splash.route) {
        // --- Rotas de Autenticação e Splash ---
        composable(Screen.Splash.route) {
            SplashScreen(
                onLogin = { nav.navigate(Screen.LoginOptions.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onSignUp = { nav.navigate(Screen.SignUp.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }
            )
        }

        composable(Screen.LoginOptions.route) {
            LoginScreen(
                onEmailClick = { nav.navigate(Screen.SignIn.route) },
                onGoogleClick = { vm.signInWithGoogle(googleSignInLauncher, googleSignInClient) },
                onSignupClick = { nav.navigate(Screen.SignUp.route) },
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                mode = AuthMode.SignIn,
                onPrimary = { email, pass, _ -> vm.signIn(email, pass) },
                onForgotPassword = {},
                onForgotNumber = {},
                onSwitch = { nav.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignUp.route) {
            SignInScreen(
                mode = AuthMode.Signup,
                onPrimary = { email, pass, name -> vm.signUp(email, pass, name ?: "") },
                onForgotPassword = {},
                onForgotNumber = {},
                onSwitch = { nav.navigate(Screen.SignIn.route) }
            )
        }

        // --- Rotas das Telas Principais do App ---
        // Acesso apenas após autenticação
        composable(Screen.Eventos.route) {
            EventosScreen(navController = nav)
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(navController = nav)
        }

        composable(Screen.CadastroEvento.route) {
            CadastroEventoScreen(navController = nav)
        }

        composable(Screen.Procurar.route) {
            ProcurarScreen(navController = nav)
        }

        // Exemplo: convites, se implementado
        composable(Screen.Convites.route) {
            ConvitesScreen(navController = nav)
        }

        // --- Rota da Tela de Detalhes do Evento com Argumento ---
        // A rota agora espera um argumento chamado 'eventoId'.
        composable(
            route = "detalhes_evento/{eventoId}",
            arguments = listOf(navArgument("eventoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getString("eventoId")
            if (eventoId != null) {
                // Instancia a tela de detalhes, passando o ID do evento
                DetalhesEventoScreen(navController = nav, eventoId = eventoId)
            } else {
                // Caso o ID do evento seja nulo, volta para a tela de eventos
                nav.popBackStack()
            }
        }


    }
}

// Função de navegação para a tela principal (Eventos)
private fun gotoEventos(nav: NavHostController) {
    nav.navigate(Screen.Eventos.route) {
        popUpTo(Screen.Splash.route) { inclusive = true }
    }
}