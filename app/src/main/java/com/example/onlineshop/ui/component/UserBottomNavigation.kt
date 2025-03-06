package com.example.onlineshop.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.navigationBarsPadding

enum class UserTab(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Transactions("Transactions", Icons.Default.List),
    Profile("Profile", Icons.Default.Person)
}

@Composable
fun UserBottomNavigation(
    selectedTab: UserTab,
    onTabSelected: (UserTab) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp), // Mengurangi padding
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            containerColor = colors.surface,
            tonalElevation = 4.dp, // Kurangi shadow agar lebih flat
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp) // Kurangi tinggi Navigation Bar
                .shadow(1.dp, shape = RoundedCornerShape(15.dp)) // Mengurangi shadow
        ) {
            UserTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val animatedAlpha by animateFloatAsState(targetValue = if (isSelected) 1f else 0.6f)

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.alpha(animatedAlpha)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp), // Kurangi ukuran ikon
                                tint = if (isSelected) colors.primary else colors.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp)) // Kurangi jarak antar ikon & teks
                            Text(
                                text = tab.title,
                                fontSize = 10.sp, // Kurangi ukuran teks
                                fontWeight = FontWeight.Normal,
                                color = if (isSelected) colors.primary else colors.onSurfaceVariant
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent // Menghilangkan highlight
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserBottomNavigation() {
    UserBottomNavigation(selectedTab = UserTab.Home, onTabSelected = {})
}
