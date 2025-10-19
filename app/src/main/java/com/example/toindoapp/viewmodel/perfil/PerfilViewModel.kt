package com.example.toindoapp.viewmodel.perfil // Ajuste o pacote se necessário

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado da UI para a tela de perfil
data class PerfilUiState(
    val nome: String = "",
    val email: String = "",
    val fotoUrl: String? = null,
    val isLoading: Boolean = true,
    val userUid : String = ""
)

class PerfilViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    // Estado para notificar a UI que o logout foi bem-sucedido
    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess = _logoutSuccess.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            _uiState.update {
                it.copy(
                    nome = currentUser.displayName ?: "Nome não informado",
                    email = currentUser.email ?: "E-mail não informado",
                    fotoUrl = currentUser.photoUrl?.toString(),
                    isLoading = false,
                    userUid = currentUser.uid
                )
            }
        } else {
            // Se por algum motivo não houver usuário, indica que não está carregando
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            _logoutSuccess.value = true
        }
    }
}