package com.example.onlineshop.data.model

data class Product(
    val id: Int = 0,
    val title: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val image: String = "",
    val quantity: Int = 0,
    val storeName: String = ""
){
    constructor() : this(0, "", 0.0, "", "", "", 0, "")
}