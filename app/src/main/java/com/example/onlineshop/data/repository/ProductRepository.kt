package com.example.onlineshop.data.repository

import com.example.onlineshop.data.model.Product
import com.example.onlineshop.data.remote.FakeStoreApi

class ProductRepository(private val api: FakeStoreApi) {
    suspend fun getProducts(): List<Product> {
        return api.getProducts()
    }
}
