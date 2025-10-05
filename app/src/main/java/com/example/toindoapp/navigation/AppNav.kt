package com.example.toindoapp.navigation

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.toindoapp.R
import com.example.toindoapp.auth.AuthViewModel
import com.example.toindoapp.telas.AuthMode
import com.example.toindoapp.telas.ConvitesScreen
import com.example.toindoapp.telas.ProfileScreen
import com.example.toindoapp.telas.SignInScreen
import com.example.toindoapp.telas.SplashScreen
import com.example.toindoapp.telas.EventosScreen // Verifique se o import está correto
import com.example.toindoapp.telas.CadastroEventoScreen
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
            // ### MUDANÇA PRINCIPAL AQUI ###
            // Antes: gotoProfile(nav)
            // Agora, o usuário é direcionado para a tela de Eventos.
            gotoEventos(nav)
        }
    }

    // Grafo de Navegação (a estrutura das rotas permanece a mesma)
    NavHost(navController = nav, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onLogin = { nav.navigate(Screen.LoginOptions.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onSignUp = { nav.navigate(Screen.SignUp.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }
            )
        }

        composable(Screen.LoginOptions.route) {
            LoginScreen(
                onEmailClick = { nav.navigate(Screen.SignIn.route) },
                onNumberClick = { /* Lógica para número */ },
                onGoogleClick = { vm.signInWithGoogle(googleSignInLauncher, googleSignInClient) },
                onSignupClick = { nav.navigate(Screen.SignUp.route) },
            )
        }

        // ### CORREÇÃO 1: SignInScreen ###
        // A tela de SignIn/SignUp não recebe o ViewModel diretamente.
        // Ela recebe funções (lambdas) para cada ação.
        composable(Screen.SignIn.route) {
            SignInScreen(
                mode = AuthMode.SignIn,
                onPrimary = { email, pass -> vm.signIn(email, pass) },
                onForgotPassword = {},
                onForgotNumber = {},
                onSwitch = { nav.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignUp.route) {
            SignInScreen(
                mode = AuthMode.Signup,
                onPrimary = { email, pass -> vm.signUp(email, pass) },
                onForgotPassword = {},
                onForgotNumber = {},
                onSwitch = { nav.navigate(Screen.SignIn.route) }
            )
        }

        // --- Telas Principais ---

        composable(Screen.Eventos.route) {
            EventosScreen(navController = nav)
        }

        // ### CORREÇÃO 2: ProfileScreen ###
        // A tela de Perfil não recebe o ViewModel como parâmetro,
        // apenas o NavController e as funções de clique.
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = nav, // Passando o NavController
                onNotification = {},
                onCalendar = {},
                onGallery = {},
                onPlaylist = {},
                onShare = {},
                onLogout = {
                    vm.signOut(googleSignInClient)
                    nav.navigate(Screen.LoginOptions.route) {
                        popUpTo(Screen.Eventos.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CadastroEvento.route) { // <-- Adicione esta composable
            CadastroEventoScreen(navController = nav)
        }

        composable(Screen.Procurar.route) { ProcurarScreen() }
        composable(Screen.Convites.route) { ConvitesScreen() }
    }
}

// Função de navegação para a tela principal (Eventos)
private fun gotoEventos(nav: NavHostController) {
    nav.navigate(Screen.Eventos.route) {
        popUpTo(Screen.Splash.route) { inclusive = true }
    }
}