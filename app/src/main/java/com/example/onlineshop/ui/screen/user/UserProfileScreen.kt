package com.example.onlineshop.ui.screen.user

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onlineshop.data.lib.DataStoreManager
import com.example.onlineshop.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun UserProfileScreen(
    authRepository: AuthRepository,
    dataStoreManager: DataStoreManager,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Profil Pengguna",
            style = MaterialTheme.typography.h6
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Nama: John Doe")
        Text("Email: johndoe@example.com")

        Button(
            onClick = {
                coroutineScope.launch {
                    authRepository.logout(dataStoreManager)
                    onLogout()
                }
            }
        ) {
            Text("Logout")
        }
    }
}
