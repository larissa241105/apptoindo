package com.example.toindoapp.data.eventos

import com.google.firebase.firestore.DocumentId
enum class StatusConvite {
    PENDENTE,
    ACEITO,
    RECUSADO
}


data class Convite(
    @DocumentId // Esta anotação mapeia o ID do documento do Firestore para este campo
    val id: String = "",

    val nomeDoEvento: String = "",
    val quemConvidou: String = "", // Pode ser o nome ou UID do remetente
    val convidadoUid: String = "", // UID do usuário que está recebendo o convite
    val eventoId: String = "", // ID do evento para futura referência,
    val status: String = "PENDENTE" // PENDENTE, ACEITO, RECUSADO
) {
    // Adicione um construtor vazio, necessário para o Firestore
    constructor() : this("", "", "", "", "", "PENDENTE")
}