package com.example.onlineshop.ui.screen.user

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.onlineshop.data.model.Product
import com.example.onlineshop.data.repository.ProductRepository
import com.example.onlineshop.ui.component.CategoriesSection
import com.example.onlineshop.ui.component.HomeTopAppBar
import com.example.onlineshop.ui.component.LoadingIndicator
import com.example.onlineshop.ui.component.ProductGrid
import com.example.onlineshop.ui.component.PromotionalBanner
import kotlinx.coroutines.launch

@Composable
fun UserHomeScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("All") }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    val categories = listOf("All", "Electronics", "Men's Clothing", "Women's Clothing", "Jewelery")

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val productRepository = ProductRepository()
                productList = productRepository.getProductsFromFirestore()
            } finally {
                isLoading = false
            }
        }
    }

    val filteredList = productList.filter {
        (selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)) &&
                it.title.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        backgroundColor = Color.White,
        topBar = {
            HomeTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onCartClick = { /* TODO: Navigate to Cart */ }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Promotional Banner
                PromotionalBanner()

                // Categories Section
                Text(
                    text = "Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                CategoriesSection(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                // Products Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Products",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "See All",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { /* TODO: Navigate to all products */ }
                    )
                }

                if (isLoading) {
                    LoadingIndicator()
                } else if (filteredList.isEmpty()) {
                    EmptyProductsView(query = searchQuery, category = selectedCategory)
                } else {
                    ProductGrid(
                        products = filteredList,
                        navController
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyProductsView(query: String, category: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Products Found",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (query.isNotEmpty())
                "We couldn't find any products matching \"$query\""
            else if (category != "All")
                "We couldn't find any products in the $category category"
            else
                "Try searching for something else",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}
