package com.example.onlineshop.ui.screen

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onlineshop.data.lib.DataStoreManager
import com.example.onlineshop.data.repository.AuthRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    authRepository: AuthRepository,
    dataStoreManager: DataStoreManager,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Admin Panel",
            style = MaterialTheme.typography.h6
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            coroutineScope.launch {
                authRepository.logout(dataStoreManager)
                onLogout()
            }
        }) {
            Text("Logout")
        }
    }
}
