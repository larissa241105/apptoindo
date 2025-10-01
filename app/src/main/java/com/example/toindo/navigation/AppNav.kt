package com.example.toindo.navigation

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
import com.example.toindo.R
import com.example.toindo.auth.AuthViewModel
import com.example.toindo.telas.AuthMode
import com.example.toindo.telas.LoginScreen
import com.example.toindo.telas.ProfileScreen
import com.example.toindo.telas.SignInScreen
import com.example.toindo.telas.SplashScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun AppNav(vm: AuthViewModel = viewModel()) {
    val nav = rememberNavController()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    // Launcher para o resultado do Login com Google
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

    // Cliente para login e logout do Google
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
            gotoProfile(nav)
        }
    }

    // Grafo de Navegação
    NavHost(navController = nav, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            // A SplashScreen original não usa o ViewModel, então passamos lambdas
            SplashScreen(
                onLogin = {
                    nav.navigate(Screen.LoginOptions.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onSignUp = {
                    // A SplashScreen também precisa de um onSignUp
                    nav.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LoginOptions.route) {
            LoginScreen(
                onEmailClick = { nav.navigate(Screen.SignIn.route) },
                onNumberClick = { /* Lógica para número de telefone */ },
                onGoogleClick = { vm.signInWithGoogle(googleSignInLauncher, googleSignInClient) },
                onSignupClick = { nav.navigate(Screen.SignUp.route) },
            )
        }

        composable(Screen.SignIn.route) {
            // Corrigindo os parâmetros para SignInScreen
            SignInScreen(
                mode = AuthMode.SignIn,
                onPrimary = { email, pass -> vm.signIn(email, pass) },
                onForgotPassword = {},
                onForgotNumber = {},
                onSwitch = { nav.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignUp.route) {
            // Corrigindo os parâmetros para SignUp
            SignInScreen(
                mode = AuthMode.Signup,
                onPrimary = { email, pass -> vm.signUp(email, pass) },
                onForgotPassword = {}, // Adicionando parâmetros que faltavam
                onForgotNumber = {}, // Adicionando parâmetros que faltavam
                onSwitch = { nav.navigate(Screen.SignIn.route) }
            )
        }

        composable(Screen.Profile.route) {
            // Corrigindo os parâmetros para ProfileScreen
            ProfileScreen(
                onNotification = {},
                onCalendar = {},
                onGallery = {},
                onPlaylist = {},
                onShare = {},
                onLogout = {
                    vm.signOut(googleSignInClient)
                    nav.navigate(Screen.LoginOptions.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

// Função de navegação para o perfil
private fun gotoProfile(nav: NavHostController) {
    nav.navigate(Screen.Profile.route) {
        // Limpa a pilha de navegação para o usuário não voltar para a Splash ou Login
        popUpTo(Screen.LoginOptions.route) {
            inclusive = true
        }
    }
}