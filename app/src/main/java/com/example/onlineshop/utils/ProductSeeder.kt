package com.example.onlineshop.utils

import android.util.Log
import com.example.onlineshop.data.remote.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore

object ProductSeeder {
    private val db = FirebaseFirestore.getInstance()

    private val storeNames = listOf(
        "Toko Elektronik Jaya",
        "Fashion Trendy",
        "Gadget Store",
        "Super Mart",
        "Buku & Stationery"
    )

    suspend fun seedProducts() {
        try {
            val products = RetrofitInstance.api.getProducts()
            for (product in products) {
                val randomStore = storeNames.random()
                val productWithQuantityAndStore = product.copy(quantity = 10, storeName = randomStore)

                db.collection("products")
                    .document(product.id.toString())
                    .set(productWithQuantityAndStore)
                    .addOnSuccessListener {
                        Log.d("FirestoreSeeder", "Produk ${product.title} berhasil ditambahkan dari $randomStore!")
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
