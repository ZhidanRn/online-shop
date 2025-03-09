package com.example.onlineshop.data.model

data class CartItem(
    val product: Product,
    val quantity: Int,
    val subtotal: Double
) {
    constructor() : this(Product(), 0, 0.0)
}