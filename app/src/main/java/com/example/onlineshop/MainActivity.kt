package com.example.onlineshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.onlineshop.data.lib.DataStoreManager
import com.example.onlineshop.data.repository.AuthRepository
import com.example.onlineshop.ui.screen.AuthScreen
import com.example.onlineshop.ui.screen.AuthViewModel
import com.example.onlineshop.ui.screen.HomeScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreManager = DataStoreManager(this)

        setContent {
            val authRepository = AuthRepository()

            val role by dataStoreManager.userRole.collectAsState(initial = null)
            val isLoggedIn by dataStoreManager.isLoggedIn.collectAsState(initial = false)

            if (!isLoggedIn || role == null) {
                AuthScreen(
                    viewModel = AuthViewModel(
                        authRepository = authRepository,
                        dataStoreManager = dataStoreManager
                    ),
                    onLoginSuccess = { email, userRole ->
                        lifecycleScope.launch {
                            dataStoreManager.saveUserSession(email, userRole.toString())
                        }
                    }
                )
            } else {
                HomeScreen(
                    role = role!!,
                    authRepository = authRepository,
                    dataStoreManager = dataStoreManager,
                    onLogout = {
                        lifecycleScope.launch {
                            dataStoreManager.clearSession()
                        }
                    }
                )
            }
        }
    }
}
