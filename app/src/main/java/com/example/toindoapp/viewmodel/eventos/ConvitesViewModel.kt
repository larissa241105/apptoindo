// Em: viewmodel/ConvitesViewModel.kt
package com.example.toindoapp.viewmodel.eventos

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.toindoapp.data.eventos.Convite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * ViewModel para gerenciar o estado e as ações da tela de convites com integração real ao Firebase.
 */
class ConvitesViewModel : ViewModel() {

    // Instâncias do Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Lista de convites que a UI irá observar
    val convites = mutableStateOf<List<Convite>>(emptyList())
    val isLoading = mutableStateOf(true) // Estado para mostrar um loading inicial

    // Referência para o listener do Firestore para que possamos removê-lo depois
    private var convitesListener: ListenerRegistration? = null

    init {
        // Inicia a escuta por convites em tempo real
        carregarConvitesEmTempoReal()
    }

    /**
     * Anexa um listener ao Firestore que atualiza a lista de convites em tempo real.
     * Sempre que um convite para o usuário atual for adicionado, modificado ou removido,
     * esta função será acionada e a UI será atualizada automaticamente.
     */
    private fun carregarConvitesEmTempoReal() {
        isLoading.value = true
        val usuarioAtualUid = auth.currentUser?.uid

        // Se não houver usuário logado, não há o que fazer.
        if (usuarioAtualUid == null) {
            Log.w("ConvitesViewModel", "Usuário não autenticado.")
            isLoading.value = false
            return
        }

        // Query: buscar na coleção "convites" onde o campo "convidadoUid" seja igual ao UID do nosso usuário
        // e o "status" seja "PENDENTE".
        val query = db.collection("convites")
            .whereEqualTo("convidadoUid", usuarioAtualUid)
            .whereEqualTo("status", "PENDENTE")

        convitesListener = query.addSnapshotListener { snapshot, error ->
            // Se ocorrer um erro na consulta
            if (error != null) {
                Log.e("ConvitesViewModel", "Erro ao ouvir os convites.", error)
                isLoading.value = false
                return@addSnapshotListener
            }

            // Se o snapshot não for nulo, processamos os dados
            if (snapshot != null) {
                // Converte todos os documentos recebidos em uma lista de objetos Convite
                val listaDeConvites = snapshot.toObjects(Convite::class.java)
                convites.value = listaDeConvites
                Log.d("ConvitesViewModel", "Convites carregados: ${listaDeConvites.size}")
            }
            isLoading.value = false
        }
    }

    /**
     * Atualiza o status de um convite para "ACEITO" no Firestore.
     * O listener em tempo real irá remover automaticamente este item da lista de pendentes.
     *
     * @param conviteId O ID do documento do convite no Firestore.
     */
    fun aceitarConvite(conviteId: String) {
        db.collection("convites").document(conviteId)
            .update("status", "ACEITO")
            .addOnSuccessListener {
                Log.d("ConvitesViewModel", "Convite $conviteId aceito com sucesso!")
                // Não é preciso remover da lista manualmente. O listener já faz isso!
            }
            .addOnFailureListener { e ->
                Log.e("ConvitesViewModel", "Erro ao aceitar o convite $conviteId", e)
            }
    }

    /**
     * Deleta o documento do convite do Firestore.
     * O listener em tempo real irá remover automaticamente este item da lista de pendentes.
     *
     * @param conviteId O ID do documento do convite no Firestore.
     */
    fun recusarConvite(conviteId: String) {
        db.collection("convites").document(conviteId)
            .delete()
            .addOnSuccessListener {
                Log.d("ConvitesViewModel", "Convite $conviteId recusado (deletado) com sucesso!")
                // Não é preciso remover da lista manualmente. O listener já faz isso!
            }
            .addOnFailureListener { e ->
                Log.e("ConvitesViewModel", "Erro ao recusar o convite $conviteId", e)
            }
    }

    /**
     * Este método é chamado quando o ViewModel está prestes a ser destruído.
     * É CRUCIAL remover o listener do Firestore para evitar vazamentos de memória e
     * cobranças desnecessárias.
     */
    override fun onCleared() {
        super.onCleared()
        convitesListener?.remove()
        Log.d("ConvitesViewModel", "Listener de convites removido.")
    }
}