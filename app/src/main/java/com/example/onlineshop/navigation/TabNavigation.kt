package com.example.onlineshop.navigation

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.onlineshop.data.lib.DataStoreManager
import com.example.onlineshop.data.repository.AuthRepository
import com.example.onlineshop.data.repository.UserProfileRepository
import com.example.onlineshop.ui.component.UserBottomNavigation
import com.example.onlineshop.ui.component.UserTab
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.onlineshop.ui.screen.user.UserHomeScreen
import com.example.onlineshop.ui.screen.user.UserProfileScreen
import com.example.onlineshop.ui.screen.user.UserTransactionScreen
import com.example.onlineshop.ui.viewModel.TransactionViewModel

@Composable
fun TabNavigation(
    authRepository: AuthRepository,
    userProfileRepository: UserProfileRepository,
    dataStoreManager: DataStoreManager,
    onLogout: () -> Unit,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(UserTab.Home) }
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight

    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }

    Scaffold(
        bottomBar = {
            UserBottomNavigation(selectedTab) { newTab ->
                selectedTab = newTab
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                UserTab.Home -> UserHomeScreen(navController)
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
