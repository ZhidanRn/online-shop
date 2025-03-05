package com.example.onlineshop.ui.screen

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminScreen() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Admin Panel",
            style = MaterialTheme.typography.h6
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Navigate to product management screen */ }
        ) {
            Text("Kelola Produk")
        }
    }
}
