package com.example.onlineshop.ui.screen.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Product(val id: Int, val name: String, val price: String)

@Composable
fun UserHomeScreen() {
    val productList = listOf(
        Product(1, "Laptop", "$1000"),
        Product(2, "Smartphone", "$700"),
        Product(3, "Headphone", "$150")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Daftar Produk:",
            style = MaterialTheme.typography.h6
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(productList) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = product.name, style = MaterialTheme.typography.body1)
                        Text(text = product.price, style = MaterialTheme.typography.body2)
                    }
                }
            }
        }
    }
}
