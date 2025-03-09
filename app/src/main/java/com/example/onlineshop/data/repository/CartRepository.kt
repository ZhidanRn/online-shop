package com.example.onlineshop.data.repository

import com.example.onlineshop.data.model.CartItem
import com.example.onlineshop.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount.asStateFlow()

    // Firebase references
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Get current user ID
    private val userId: String
        get() = auth.currentUser?.uid ?: "guest"

    // Collection reference
    private val cartCollection
        get() = firestore.collection("users").document(userId).collection("cart")

    suspend fun loadCartFromFirestore() {
        try {
            val snapshot = cartCollection.get().await()
            val items = snapshot.documents.mapNotNull { doc ->
                val productMap = doc.get("product") as? Map<String, Any> ?: return@mapNotNull null
                val product = Product(
                    id = (productMap["id"] as? Long)?.toInt() ?: return@mapNotNull null,
                    title = productMap["title"] as? String ?: return@mapNotNull null,
                    price = (productMap["price"] as? Number)?.toDouble() ?: return@mapNotNull null,
                    description = productMap["description"] as? String ?: "",
                    category = productMap["category"] as? String ?: "",
                    image = productMap["image"] as? String ?: "",
                    quantity = (productMap["quantity"] as? Long)?.toInt() ?: 0
                )
                val quantity = (doc.getLong("quantity") ?: 1).toInt()
                CartItem(product, quantity, product.price * quantity)
            }
            _cartItems.value = items
            updateTotals()
        } catch (e: Exception) {
            // Handle error
            println("Error loading cart: ${e.message}")
        }
    }

    suspend fun addToCart(product: Product, quantity: Int) {
        // Update local state
        val currentItems = _cartItems.value.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.product.id == product.id }

        if (existingItemIndex != -1) {
            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            currentItems.add(CartItem(product, quantity, product.price * quantity))
        }

        _cartItems.value = currentItems
        updateTotals()

        // Save to Firestore
        try {
            val cartItem = if (existingItemIndex != -1) currentItems[existingItemIndex] else currentItems.last()
            val itemData = hashMapOf(
                "product" to hashMapOf(
                    "id" to product.id,
                    "title" to product.title,
                    "price" to product.price,
                    "description" to product.description,
                    "category" to product.category,
                    "image" to product.image,
                    "quantity" to product.quantity
                ),
                "quantity" to cartItem.quantity
            )
            cartCollection.document(product.id.toString()).set(itemData).await()
        } catch (e: Exception) {
            println("Error adding to cart: ${e.message}")
        }
    }

    suspend fun removeFromCart(productId: Int) {
        // Update local state
        _cartItems.update { items ->
            items.filter { it.product.id != productId }
        }
        updateTotals()

        // Remove from Firestore
        try {
            cartCollection.document(productId.toString()).delete().await()
        } catch (e: Exception) {
            println("Error removing from cart: ${e.message}")
        }
    }

    suspend fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }

        // Update local state
        _cartItems.update { items ->
            items.map {
                if (it.product.id == productId) it.copy(quantity = quantity)
                else it
            }
        }
        updateTotals()

        // Update in Firestore
        try {
            cartCollection.document(productId.toString())
                .update("quantity", quantity).await()
        } catch (e: Exception) {
            println("Error updating quantity: ${e.message}")
        }
    }

    suspend fun clearCart() {
        // Update local state
        _cartItems.value = emptyList()
        updateTotals()

        // Clear in Firestore
        try {
            val batch = firestore.batch()
            val documents = cartCollection.get().await()
            for (document in documents) {
                batch.delete(document.reference)
            }
            batch.commit().await()
        } catch (e: Exception) {
            println("Error clearing cart: ${e.message}")
        }
    }

    private fun updateTotals() {
        val items = _cartItems.value
        val total = items.sumOf { it.product.price * it.quantity }
        _totalPrice.value = total
        val count = items.sumOf { it.quantity }
        _itemCount.value = count
    }
}