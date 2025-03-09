package com.example.onlineshop.ui.screen.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.onlineshop.utils.formatCurrency
import com.example.onlineshop.ui.viewModel.CartViewModel
import com.example.onlineshop.ui.viewModel.TransactionViewModel

@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel(),
    onPaymentSuccess: () -> Unit = {},
    onBackToCart: () -> Unit = {}
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val itemCount by cartViewModel.itemCount.collectAsState()
    val isOrderPlaced by transactionViewModel.isOrderPlaced.collectAsState()

    var showConfirmationScreen by remember { mutableStateOf(false) }

    if (showConfirmationScreen) {
        ConfirmationPaymentScreen(
            totalAmount = totalPrice,
            expirationTime = System.currentTimeMillis() + 60000,
            onPaymentSuccess = onPaymentSuccess
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Checkout") },
                    navigationIcon = {
                        IconButton(onClick = onBackToCart) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back to Cart"
                            )
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White,
                    elevation = 4.dp
                )
            },
            bottomBar = {
                if (!isOrderPlaced) {
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
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = "Total: " + formatCurrency(totalPrice),
                                        style = MaterialTheme.typography.subtitle1,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colors.primary
                                    )
                                    Text(
                                        text = "$itemCount item(s) + shipping",
                                        style = MaterialTheme.typography.caption,
                                        color = Color.Gray
                                    )
                                }

                                Button(
                                    onClick = {
                                        transactionViewModel.placeOrder(cartItems, totalPrice)
                                        showConfirmationScreen = true // ✅ Set state agar tampil layar pembayaran
                                    },
                                    modifier = Modifier
                                        .height(48.dp)
                                        .width(140.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.primary
                                    )
                                ) {
                                    Text(
                                        text = "Place Order",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    CheckoutSection(title = "Order Summary") {
                        CartItems(cartViewModel)
                    }
                }
            }
        }
    }
}


@Composable
fun CheckoutSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun CartItems(
    cartViewModel: CartViewModel = viewModel(),
) {
    val cartItems by cartViewModel.cartItems.collectAsState(initial = emptyList())

    Column {
        cartItems.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = item.product.image
                    ),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = item.product.title, fontWeight = FontWeight.Bold)
                    Text(text = "${item.quantity} x ${formatCurrency(item.product.price)}")
                }

                Text(
                    text = formatCurrency(item.quantity * item.product.price),
                    fontWeight = FontWeight.Bold
                )
            }
            Divider()
        }
    }
}
