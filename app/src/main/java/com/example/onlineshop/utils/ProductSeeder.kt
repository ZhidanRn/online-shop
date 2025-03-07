package com.example.onlineshop.utils

import android.annotation.SuppressLint
import android.util.Log
import com.example.onlineshop.data.remote.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore

object ProductSeeder {
    private val db = FirebaseFirestore.getInstance()

    suspend fun seedProducts() {
        try {
            val products = RetrofitInstance.api.getProducts()
            for (product in products) {
                val productWithQuantity = product.copy(quantity = 10)
                db.collection("products")
                    .document(product.id.toString())
                    .set(productWithQuantity)
                    .addOnSuccessListener {
                        Log.d("FirestoreSeeder", "Produk ${product.title} berhasil ditambahkan!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreSeeder", "Gagal menambahkan produk: ${e.message}")
                    }
            }
        } catch (e: Exception) {
            Log.e("FirestoreSeeder", "Gagal fetch data dari API: ${e.message}")
        }
    }
}
