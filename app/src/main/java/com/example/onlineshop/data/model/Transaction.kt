package com.example.onlineshop.data.model

data class Transaction(
    val items: List<CartItem> = emptyList(), // Default value
    val totalPrice: Double = 0.0, // Default value
    val status: String = "pending", // Default value
    val timestamp: Long = System.currentTimeMillis() // Default value
) {
    // Constructor tanpa argumen
    constructor() : this(emptyList(), 0.0, "pending", System.currentTimeMillis())
}