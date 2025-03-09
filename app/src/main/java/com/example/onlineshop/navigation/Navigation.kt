package com.example.onlineshop.navigation

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlineshop.data.lib.DataStoreManager
import com.example.onlineshop.data.repository.AuthRepository
import com.example.onlineshop.data.repository.UserProfileRepository
import com.example.onlineshop.ui.screen.AdminScreen
import com.example.onlineshop.ui.screen.user.ProductDetailScreen
import com.example.onlineshop.ui.screen.user.CartScreen
import com.example.onlineshop.ui.screen.user.CheckoutScreen
import com.example.onlineshop.ui.viewModel.CartViewModel

@Composable
fun Navigation(
    role: String,
    authRepository: AuthRepository,
    userProfileRepository: UserProfileRepository,
    dataStoreManager: DataStoreManager,
    onLogout: () -> Unit,
    cartViewModel: CartViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (role == "admin") {
            AdminScreen(
                authRepository = authRepository,
                dataStoreManager = dataStoreManager,
                onLogout = onLogout
            )
        } else {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "user_screen") {
                composable("user_screen") {
                    TabNavigation(
                        authRepository = authRepository,
                        userProfileRepository = userProfileRepository,
                        dataStoreManager = dataStoreManager,
                        onLogout = onLogout,
                        navController = navController
                    )
                }
                composable("detail/{productId}") { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId")
                    ProductDetailScreen(productId, goToCart = { navController.navigate("cart") })
                }
                composable("cart") {
                    CartScreen(
                        onCheckout = { navController.navigate("checkout") }
                    )
                }
                composable("checkout") {
                    CheckoutScreen()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
