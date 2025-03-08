package com.example.onlineshop.data.repository

import com.example.onlineshop.data.model.CartItem
import com.example.onlineshop.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount.asStateFlow()

    suspend fun addToCart(product: Product, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()

        val existingItemIndex = currentItems.indexOfFirst { it.product.id == product.id }

        if (existingItemIndex != -1) {
            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            currentItems.add(CartItem(product, quantity))
        }

        _cartItems.value = currentItems
        updateTotals()
    }

    fun removeFromCart(productId: Int) {
        _cartItems.update { items ->
            items.filter { it.product.id != productId }
        }
        updateTotals()
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }

        _cartItems.update { items ->
            items.map {
                if (it.product.id == productId) it.copy(quantity = quantity)
                else it
            }
        }
        updateTotals()
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        updateTotals()
    }

    private fun updateTotals() {
        val items = _cartItems.value

        val total = items.sumOf { it.product.price * it.quantity }
        _totalPrice.value = total

        val count = items.sumOf { it.quantity }
        _itemCount.value = count
    }
}