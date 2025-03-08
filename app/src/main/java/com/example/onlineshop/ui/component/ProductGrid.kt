package com.example.onlineshop.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.onlineshop.data.model.Product
import com.example.onlineshop.utils.formatCurrency

@Composable
fun ProductGrid(
    products: List<Product>,
    navController: NavController
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products.chunked(2)) { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowProducts.forEach { product ->
                    EnhancedProductCard(
                        product = product,
                        modifier = Modifier.weight(1f),
                        navController
                    )
                }

                // Add an empty spacer for odd number of products to keep the grid balanced
                if (rowProducts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun EnhancedProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = Color.White,
        modifier = modifier
            .width(180.dp)
            .height(320.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
            .clickable { navController.navigate("detail/${product.id}") }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.White)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = product.image
                    ),
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )

                // Stock indicator if stock is low
                if (product.quantity in 1..5) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE57373))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Stock ${product.quantity}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .weight(1f)
            ) {
                // Product title
                Text(
                    text = product.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price section
                Text(
                    text = formatCurrency(product.price),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description preview
                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                // Stock info and Add to Cart button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stock information
                    Text(
                        text = when {
                            product.quantity > 10 -> "In Stock"
                            product.quantity > 0 -> "Limited Stock"
                            else -> "Out of Stock"
                        },
                        fontSize = 12.sp,
                        color = when {
                            product.quantity > 10 -> Color(0xFF4CAF50)
                            product.quantity > 0 -> Color(0xFFFFA000)
                            else -> Color(0xFFE57373)
                        },
                        fontWeight = FontWeight.Medium
                    )

                    // Add to cart button (only show if in stock)
//                    if (product.quantity > 0) {
//                        Button(
//                            onClick = { /* TODO: Add to cart */ },
//                            modifier = Modifier
//                                .height(36.dp)
//                                .clip(RoundedCornerShape(8.dp)),
//                            colors = ButtonDefaults.buttonColors(
//                                backgroundColor = MaterialTheme.colorScheme.primary,
//                                contentColor = Color.White
//                            ),
//                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
//                        ) {
//                            Icon(
//                                Icons.Default.ShoppingCart,
//                                contentDescription = "Add to cart",
//                                modifier = Modifier.size(18.dp)
//                            )
//                        }
//                    }
                }
            }
        }
    }
}