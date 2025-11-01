package com.example.toindoapp.viewmodel.eventos

import Evento // Certifique-se de que a importação do seu modelo de dados está correta
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

// Estado da UI para o formulário (sem alterações)
data class CadastroEventoUiState(
    val nome: String = "",
    val data: String = "",
    val horario: String = "",
    val local: String = "",
    val preco: String = "",
    val isGratuito: Boolean = false,
    val descricao: String = "",
    val categoria: String = "",
    val imagemUri: String? = null,
    val publico: Boolean = true
)

// Estado do processo de salvamento (sem alterações)
sealed interface SaveState {
    object Idle : SaveState
    object Loading : SaveState
    object Success : SaveState
    data class Error(val message: String) : SaveState

}

class CadastroEventoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CadastroEventoUiState())
    val uiState = _uiState.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()

    // Funções de update dos campos (sem alterações)
    fun onNomeChange(novoNome: String) { _uiState.update { it.copy(nome = novoNome) } }
    fun onDataChange(novaData: String) { _uiState.update { it.copy(data = novaData) } }
    fun onHorarioChange(novoHorario: String) { _uiState.update { it.copy(horario = novoHorario) } }
    fun onLocalChange(novoLocal: String) { _uiState.update { it.copy(local = novoLocal) } }
    fun onPrecoChange(novoPreco: String) { _uiState.update { it.copy(preco = novoPreco) } }
    fun onGratuitoChange(isGratuito: Boolean) { _uiState.update { it.copy(isGratuito = isGratuito) } }
    fun onCategoriaChange(novaCategoria: String) { _uiState.update { it.copy(categoria = novaCategoria) } }
    fun onDescricaoChange(novaDescricao: String) { _uiState.update { it.copy(descricao = novaDescricao) } }
    fun onImagemSelected(uri: String?) { _uiState.update { it.copy(imagemUri = uri) } }
    fun onPublicoChange(isPublico: Boolean) { _uiState.update { it.copy(publico = isPublico) } }


    fun salvarEvento() {
        if (_saveState.value is SaveState.Loading) return

        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            // Se não houver usuário logado, não podemos salvar. Mostra um erro.
            _saveState.value = SaveState.Error("Você precisa estar logado para criar um evento.")
            return
        }
        val estadoAtual = _uiState.value
        if (estadoAtual.nome.isBlank() || estadoAtual.data.isBlank() || estadoAtual.local.isBlank()) {
            _saveState.value = SaveState.Error("Nome, data e local são obrigatórios.")
            return
        }

        viewModelScope.launch {
            _saveState.value = SaveState.Loading
            println("DEBUG: [1] Iniciando o processo de salvamento.")
            try {
                // Adiciona um timeout de 15 segundos à operação
                withTimeout(15000L) { // 15000 milissegundos = 15 segundos
                    val userDoc = Firebase.firestore.collection("users").document(userId).get().await()
                    val creatorName = userDoc.getString("nome") ?: "Anônimo"
                    val evento = Evento(
                        nome = estadoAtual.nome.trim(),
                        data = estadoAtual.data.trim(),
                        horario = estadoAtual.horario.trim(),
                        local = estadoAtual.local.trim(),
                        preco = if (estadoAtual.isGratuito) 0.0 else estadoAtual.preco.toDoubleOrNull() ?: 0.0,
                        isGratuito = estadoAtual.isGratuito,
                        descricao = estadoAtual.descricao.trim(),
                        categoria = estadoAtual.categoria,
                        publico = estadoAtual.publico,
                        creatorId = userId,
                        creatorName = creatorName
                    )

                    println("DEBUG: [2] Objeto Evento criado. Tentando enviar para o Firestore.")
                    Firebase.firestore.collection("eventos")
                        .add(evento)
                        .await() // Se travar, vai travar aqui

                    println("DEBUG: [3] Sucesso! Dados salvos no Firestore.")
                    _saveState.value = SaveState.Success
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                // Este bloco será executado se a operação demorar mais de 15 segundos
                println("DEBUG: [ERRO] A operação excedeu o tempo limite (Timeout). Verifique a conexão e as regras de segurança.")
                _saveState.value = SaveState.Error("A conexão demorou muito para responder. Tente novamente.")
            } catch (e: Exception) {
                // Captura qualquer outro erro que possa ocorrer
                println("DEBUG: [ERRO] Ocorreu uma exceção: ${e.message}")
                _saveState.value = SaveState.Error(e.message ?: "Ocorreu um erro desconhecido.")
            }
        }
    }


    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }
}