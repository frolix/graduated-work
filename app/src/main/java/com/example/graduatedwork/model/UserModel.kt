package com.example.graduatedwork.model

data class UserModel(
    val userName: String,
    val userEmail: String,
    val profilePicture: String?
) {
    constructor() : this("","",null)

}