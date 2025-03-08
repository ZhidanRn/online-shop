package com.example.onlineshop.data.repository

import com.example.onlineshop.data.model.Product
import com.example.onlineshop.data.remote.FakeStoreApi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository() {
    private val db = FirebaseFirestore.getInstance()


    suspend fun addProductToFirestore(product: Product) {
        try {
            db.collection("products")
                .document(product.id.toString())
                .set(product)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to add product to Firestore: ${e.message}")
        }
    }

    suspend fun getProductsFromFirestore(): List<Product> {
        return try {
            val snapshot = db.collection("products").get().await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch products from Firestore: ${e.message}")
        }
    }

    suspend fun getProductById(productId: Int): Product? {
        return try {
            val document = db.collection("products").document(productId.toString()).get().await()
            document.toObject(Product::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch product with ID $productId: ${e.message}")
        }
    }
}
