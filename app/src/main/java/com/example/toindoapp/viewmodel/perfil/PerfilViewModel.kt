package com.example.toindoapp.viewmodel.perfil // Ajuste o pacote se necessário

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


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

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess = _logoutSuccess.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            // Se não há usuário, não há nada a fazer
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        // 1. Define os dados imediatos (da Autenticação)
        _uiState.update {
            it.copy(
                email = currentUser.email ?: "E-mail não informado",
                fotoUrl = currentUser.photoUrl?.toString(),
                userUid = currentUser.uid,
                isLoading = true
            )
        }

        FirebaseFirestore.getInstance().collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    val nomeFromDb = document.getString("nome") ?: "Nome não informado"
                    _uiState.update {
                        it.copy(
                            nome = nomeFromDb,
                            isLoading = false
                        )
                    }
                } else {

                    _uiState.update {
                        it.copy(
                            nome = "Nome não informado",
                            isLoading = false
                        )
                    }
                }
            }
            .addOnFailureListener {
               
                _uiState.update {
                    it.copy(
                        nome = "Erro ao buscar nome",
                        isLoading = false
                    )
                }
            }
    }

    fun logout() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            _logoutSuccess.value = true
        }
    }
}