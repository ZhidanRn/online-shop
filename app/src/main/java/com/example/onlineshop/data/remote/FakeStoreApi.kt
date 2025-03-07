package com.example.onlineshop.data.remote

import com.example.onlineshop.data.model.Product
import retrofit2.http.GET

interface FakeStoreApi {
    @GET("products")
    suspend fun getProducts(): List<Product>
}