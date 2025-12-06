// Em: viewmodel/ConvitesViewModel.kt
package com.example.toindoapp.viewmodel.eventos

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.toindoapp.data.eventos.Convite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class ConvitesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val convites = mutableStateOf<List<Convite>>(emptyList())
    val isLoading = mutableStateOf(true)

    private var convitesListener: ListenerRegistration? = null

    init {

        carregarConvitesEmTempoReal()
    }


    private fun carregarConvitesEmTempoReal() {
        isLoading.value = true
        val usuarioAtualUid = auth.currentUser?.uid


        if (usuarioAtualUid == null) {
            Log.w("ConvitesViewModel", "Usuário não autenticado.")
            isLoading.value = false
            return
        }


        val query = db.collection("convites")
            .whereEqualTo("convidadoUid", usuarioAtualUid)
            .whereEqualTo("status", "PENDENTE")

        convitesListener = query.addSnapshotListener { snapshot, error ->

            if (error != null) {
                Log.e("ConvitesViewModel", "Erro ao ouvir os convites.", error)
                isLoading.value = false
                return@addSnapshotListener
            }


            if (snapshot != null) {

                val listaDeConvites = snapshot.toObjects(Convite::class.java)
                convites.value = listaDeConvites
                Log.d("ConvitesViewModel", "Convites carregados: ${listaDeConvites.size}")
            }
            isLoading.value = false
        }
    }


    fun aceitarConvite(conviteId: String) {
        db.collection("convites").document(conviteId)
            .update("status", "ACEITO")
            .addOnSuccessListener {
                Log.d("ConvitesViewModel", "Convite $conviteId aceito com sucesso!")

            }
            .addOnFailureListener { e ->
                Log.e("ConvitesViewModel", "Erro ao aceitar o convite $conviteId", e)
            }
    }


    fun recusarConvite(conviteId: String) {
        db.collection("convites").document(conviteId)
            .delete()
            .addOnSuccessListener {
                Log.d("ConvitesViewModel", "Convite $conviteId recusado (deletado) com sucesso!")

            }
            .addOnFailureListener { e ->
                Log.e("ConvitesViewModel", "Erro ao recusar o convite $conviteId", e)
            }
    }


    override fun onCleared() {
        super.onCleared()
        convitesListener?.remove()
        Log.d("ConvitesViewModel", "Listener de convites removido.")
    }
}