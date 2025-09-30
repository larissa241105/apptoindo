package com.example.toindo.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
    val error: String? = null
)

class AuthViewModel: ViewModel(){
    private val auth = FirebaseAuth.getInstance()
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun signIn(email: String, password: String){
        setLoading()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> if (task.isSuccessful){
                ok()
            }else{
                    fail(task.exception)
            }
            }
    }


    fun signUp(email: String, password: String){
        setLoading()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> if (task.isSuccessful){
                ok()
            }else{
                fail(task.exception)
            }
                fun signIn(email: String, password: String){
                    setLoading()
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task -> if (task.isSuccessful){
                            ok()
                        }else{
                            fail(task.exception)
                        }
                        }
                }       }
    }

    fun signOut(){
        auth.signOut()
        _state.value = AuthUiState(user = null)
    }

    private fun setLoading()
    {_state.value = _state.value.copy(isLoading = true, error = null)}
    private fun ok(){_state.value = _state.value.copy(isLoading = false, user=auth.currentUser, error=null)}
    private fun fail(ex: Exception?){
        _state.value=_state.value.copy(isLoading = false, user=null, error = mapError(ex))
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