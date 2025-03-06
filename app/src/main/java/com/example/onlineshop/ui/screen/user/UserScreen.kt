package com.example.onlineshop.ui.screen.user

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.R
import androidx.compose.ui.Modifier
import com.example.onlineshop.data.lib.DataStoreManager
import com.example.onlineshop.data.repository.AuthRepository
import com.example.onlineshop.data.repository.UserProfileRepository
import com.example.onlineshop.ui.component.UserBottomNavigation
import com.example.onlineshop.ui.component.UserTab

@Composable
fun UserScreen(
    authRepository: AuthRepository,
    userProfileRepository: UserProfileRepository,
    dataStoreManager: DataStoreManager,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(UserTab.Home) }

    Scaffold(
        bottomBar = {
            UserBottomNavigation(selectedTab) { newTab ->
                selectedTab = newTab
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                UserTab.Home -> UserHomeScreen()
                UserTab.Transactions -> UserTransactionScreen()
                UserTab.Profile -> UserProfileScreen(
                    authRepository = authRepository,
                    dataStoreManager = dataStoreManager,
                    userRepository = userProfileRepository,
                    onLogout = onLogout
                )
            }
        }
    }
}
