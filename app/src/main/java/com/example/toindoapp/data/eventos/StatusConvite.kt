package com.example.toindoapp.data.eventos

import com.google.firebase.firestore.DocumentId
enum class StatusConvite {
    PENDENTE,
    ACEITO,
    RECUSADO
}


data class Convite(
    @DocumentId
    val id: String = "",

    val nomeDoEvento: String = "",
    val quemConvidou: String = "",
    val convidadoUid: String = "",
    val eventoId: String = "",
    val status: String = "PENDENTE"
) {
    // Adicione um construtor vazio, necessário para o Firestore
    constructor() : this("", "", "", "", "", "PENDENTE")
}