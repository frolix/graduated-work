package com.example.graduatedwork.model

import java.util.*

object NotesType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Notes{
    var important: Boolean
    var idDoc: String
    val time: Date
    val userId: String
    val userRecipientId: String
    val senderName: String
    val type: String
}