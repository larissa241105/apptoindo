package com.example.toindoapp.auth

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 1. MUDANÇA AQUI: Trocamos 'isSuccess' por 'user'
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: FirebaseUser? = null
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    // 2. MUDANÇA AQUI: A função 'ok' agora armazena o usuário
    private fun ok() {
        _state.value = AuthUiState(user = auth.currentUser)
    }

    private fun fail(exception: Exception?) {
        _state.value = AuthUiState(error = mapError(exception))
    }

    fun signIn(email: String, pass: String) {
        setLoading()
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ok()
                } else {
                    fail(task.exception)
                }
            }
    }

    fun signUp(email: String, pass: String) {
        setLoading()
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ok()
                } else {
                    fail(task.exception)
                }
            }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        setLoading()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ok()
                } else {
                    fail(task.exception)
                }
            }
    }

    fun signInWithGoogle(launcher: ActivityResultLauncher<Intent>, client: GoogleSignInClient) {
        setLoading()
        launcher.launch(client.signInIntent)
    }

    fun signOut(googleSignInClient: GoogleSignInClient) {
        setLoading()
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            resetState()
        }
    }

    // 3. MUDANÇA AQUI: 'resetState' limpa o usuário e o erro
    fun resetState() {
        _state.value = AuthUiState()
    }

    private fun setLoading() {
        _state.value = AuthUiState(isLoading = true)
}

    private fun mapError(ex: Exception?): String {
        val e = ex ?: return "Authentication failed. Please try again."
        return when (e) {
            is FirebaseAuthUserCollisionException ->
                "This email is already registered. Try signing in or resetting your password."
            is FirebaseAuthInvalidCredentialsException ->
                when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
                    else -> "Invalid credentials. Please check your email and password."
                }
            is FirebaseAuthInvalidUserException ->
                when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email."
                    "ERROR_USER_DISABLED" -> "Your account has been disabled."
                    else -> "This account is not valid."
                }
            is FirebaseNetworkException ->
                "No internet connection. Please check your network and try again."
            else -> {
                val code = (e as? FirebaseAuthException)?.errorCode
                when (code) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered. Try signing in or resetting your password."
                    "ERROR_WEAK_PASSWORD"        -> "Password is too weak. Use at least 6 characters."
                    "ERROR_OPERATION_NOT_ALLOWED"-> "Email/password sign-in is disabled for this project."
                    "ERROR_TOO_MANY_REQUESTS"    -> "Too many attempts. Please try again later."
                    else -> e.localizedMessage?.substringBefore('\n')
                        ?: "Authentication failed. Please try again."
                }
            }
        }
    }

}