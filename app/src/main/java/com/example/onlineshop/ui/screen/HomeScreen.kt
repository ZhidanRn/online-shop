package com.example.onlineshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onlineshop.data.repository.AuthRepository
import com.example.onlineshop.ui.screen.user.UserScreen

@Composable
fun HomeScreen(
    role: String,
    authRepository: AuthRepository,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (role == "admin") {
            AdminScreen()
        } else {
            UserScreen()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authRepository.logout()
                onLogout()
            }
        ) {
            Text("Logout")
        }
    }
}
