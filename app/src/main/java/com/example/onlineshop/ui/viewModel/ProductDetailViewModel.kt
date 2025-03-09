package com.example.onlineshop.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.model.Product
import com.example.onlineshop.data.repository.CartRepository
import com.example.onlineshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository = CartRepository()
) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun getProductById(productId: Int) {
        viewModelScope.launch {
            try {
                _product.value = repository.getProductById(productId)
            } catch (e: Exception) {
                println("Error fetching product: ${e.message}")
            }
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(product, quantity)
            } catch (e: Exception) {
                println("Error adding to cart: ${e.message}")
            }
        }
    }
}