package com.example.onlineshop.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val role: String = "user",
    val profileImageUrl: String = ""
)
