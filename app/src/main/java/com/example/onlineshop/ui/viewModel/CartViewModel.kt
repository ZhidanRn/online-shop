package com.example.onlineshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.repository.CartRepository
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val repository = CartRepository()

    val cartItems = repository.cartItems
    val totalPrice = repository.totalPrice
    val itemCount = repository.itemCount

    init {
        viewModelScope.launch {
            repository.loadCartFromFirestore()
        }
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}
