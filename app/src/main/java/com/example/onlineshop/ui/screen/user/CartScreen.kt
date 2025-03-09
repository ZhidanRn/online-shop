package com.example.onlineshop.ui.screen.user

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onlineshop.utils.formatCurrency
import com.example.onlineshop.viewmodel.CartViewModel
import com.example.onlineshop.ui.component.CartItem

@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    onCheckout: () -> Unit = {}
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val itemCount by viewModel.itemCount.collectAsState()

    val groupedItems = cartItems.groupBy { it.product.id % 3 }

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open menu */ }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                elevation = 4.dp
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    elevation = 8.dp,
                    color = MaterialTheme.colors.surface
                ) {
                    Column {
                        Divider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Total: " + formatCurrency( totalPrice ),
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.primary
                                )

                                if (itemCount > 0) {
                                    Text(
                                        text = "$itemCount item",
                                        style = MaterialTheme.typography.caption,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Button(
                                onClick = onCheckout,
                                modifier = Modifier
                                    .height(48.dp)
                                    .width(120.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.primary
                                )
                            ) {
                                Text(
                                    text = "Checkout",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                if (cartItems.isEmpty()) {
                    EmptyCartView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        groupedItems.forEach { (storeId, storeItems) ->
                            items(storeItems) { item ->
                                CartItem(item, viewModel)
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun EmptyCartView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            tint = Color.LightGray
        )

        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start shopping to add items to your cart",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Navigate to product list */ },
            modifier = Modifier
                .height(48.dp)
                .width(200.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Text(
                text = "Shop Now",
                fontWeight = FontWeight.Bold
            )
        }
    }
}