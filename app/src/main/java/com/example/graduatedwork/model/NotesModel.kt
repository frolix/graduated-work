package com.example.graduatedwork.model

import java.util.*


data class NotesModel(
    var title: String,
    var textNotes: String ,
    override var important: Boolean,
    override var idDoc: String,
    override val time: Date,
    override val userId: String,
    override val userRecipientId: String,
    override val senderName: String,
    override val type: String = NotesType.TEXT

) :Notes{

    constructor(): this("","",false,"",Date(0),"","","","")

}