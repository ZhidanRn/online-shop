package com.example.onlineshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.onlineshop.data.repository.AuthRepository
import com.example.onlineshop.ui.screen.AuthScreen
import com.example.onlineshop.ui.screen.AuthViewModel
import com.example.onlineshop.ui.screen.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var role by remember { mutableStateOf<String?>(null) }
            val authRepository = AuthRepository()

            if (role == null) {
                AuthScreen(
                    viewModel = AuthViewModel(authRepository = authRepository),
                    onLoginSuccess = {
                        role = it
                    }
                ) { receivedRole ->
                    role = receivedRole.toString()
                }
            } else {
                HomeScreen(
                    role = role!!,
                    authRepository = authRepository,
                    onLogout = { role = null }
                )
            }
        }
    }
}