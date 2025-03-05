package com.example.onlineshop.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

// Enum untuk tab navigasi
enum class UserTab(val title: String, val icon: ImageVector) {
    Home("Beranda", Icons.Default.Home),
    Transactions("Transaksi", Icons.Default.List),
    Profile("Akun", Icons.Default.Person)
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
            .padding(12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            containerColor = colors.surface, // Gunakan warna dari theme
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .shadow(8.dp, shape = RoundedCornerShape(24.dp))
                .background(colors.surface, shape = RoundedCornerShape(24.dp))
        ) {
            UserTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                val animatedAlpha by animateFloatAsState(if (isSelected) 1f else 0.6f)

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
                                tint = if (isSelected) colors.primary else colors.onSurfaceVariant // Gunakan warna theme
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tab.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) colors.primary else colors.onSurfaceVariant
                            )
                        }
                    }
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
