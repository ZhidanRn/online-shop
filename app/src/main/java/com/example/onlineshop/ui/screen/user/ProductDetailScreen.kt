package com.example.onlineshop.ui.screen.user

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.onlineshop.data.model.Product
import com.example.onlineshop.data.repository.CartRepository
import com.example.onlineshop.data.repository.ProductRepository
import com.example.onlineshop.utils.formatCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(productId: String?) {
    val viewModel = remember { ProductDetailViewModel(ProductRepository()) }
    val product by viewModel.product.collectAsState()
    val scrollState = rememberScrollState()
    var quantity by remember { mutableStateOf(1) }
    var showAddedToCartSnackbar by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(productId) {
        productId?.toIntOrNull()?.let { id ->
            viewModel.getProductById(id)
        }
    }

    LaunchedEffect(showAddedToCartSnackbar) {
        if (showAddedToCartSnackbar) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Product added to cart",
                actionLabel = "See"
            )
            showAddedToCartSnackbar = false
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Detail Product") },
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Favorite functionality */ }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                elevation = 8.dp
            )
        },
        bottomBar = {
            product?.let { prod ->
                Surface(
                    elevation = 8.dp,
                    color = MaterialTheme.colors.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.caption
                            )
                            Text(
                                text = formatCurrency(prod.price * quantity),
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.primary
                            )
                        }

                        Button(
                            onClick = {
                                // Handle adding to cart
                                viewModel.addToCart(prod, quantity)
                                showAddedToCartSnackbar = true
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .width(180.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Cart")
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
        ) {
            AnimatedVisibility(
                visible = product == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            product?.let { prod ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Product Image Section with Rating Badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        AsyncImage(
                            model = prod.image,
                            contentDescription = "Product Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Rating Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colors.primary.copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "4.8",
                                    color = Color.White,
                                    style = MaterialTheme.typography.caption,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Product Info Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Title & Price
                        Text(
                            text = prod.title,
                            style = MaterialTheme.typography.h5,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = formatCurrency(prod.price),
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // Category & Stock with Icons
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Category,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Category",
                                        style = MaterialTheme.typography.caption,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = prod.category,
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Inventory,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Stock",
                                        style = MaterialTheme.typography.caption,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${prod.quantity} pcs",
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            }
                        }

                        // Quantity Selector
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Quantity",
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .padding(4.dp)
                                .width(150.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = if (quantity > 1) MaterialTheme.colors.primary else Color.Gray
                                )
                            }

                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                textAlign = TextAlign.Center
                            )

                            IconButton(
                                onClick = { if (quantity < prod.quantity) quantity++ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = if (quantity < prod.quantity) MaterialTheme.colors.primary else Color.Gray
                                )
                            }
                        }

                        // Description Section
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = prod.description,
                            style = MaterialTheme.typography.body2,
                            color = Color.DarkGray,
                            lineHeight = 24.sp
                        )

                        // Specification Section (Example)
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Specification",
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SpecificationRow("Material", "Premium")
                        SpecificationRow("Weight", "250 gram")
                        SpecificationRow("Dimension", "20 x 15 x 5 cm")
                        SpecificationRow("Warranty", "12 bulan")

                        // Bottom space for the bottom bar
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SpecificationRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Modifier.border(
    width: Dp,
    color: Color,
    shape: RoundedCornerShape
) = this.then(
    Modifier.drawBehind {
        drawRoundRect(
            color = color,
            style = Stroke(width = width.toPx()),
            cornerRadius = CornerRadius(x = 20f, y = 20f),
        )
    }
)

// Updated ViewModel with cart functionality
class ProductDetailViewModel(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository = CartRepository()
) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun getProductById(productId: Int) {
        viewModelScope.launch {
            try {
                _product.value = repository.getProductById(productId)
            } catch (e: Exception) {
                println("Error fetching product: ${e.message}")
            }
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(product, quantity)
            } catch (e: Exception) {
                println("Error adding to cart: ${e.message}")
            }
        }
    }
}