package com.example.onlineshop.ui.screen.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onlineshop.data.model.Transaction
import com.example.onlineshop.ui.viewModel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTransactionScreen(viewModel: TransactionViewModel = remember { TransactionViewModel() }) {
    val transactions = viewModel.transactions.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Transactions") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items( transactions.size ) { transaction ->
                TransactionItem(
                    transaction = transactions[transaction]
                )
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Status: ${transaction.status}")
            Text(text = "Total Price: $${transaction.totalPrice}")
            Text(text = "Timestamp: ${transaction.timestamp}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Items:")
            transaction.items.forEach { item ->
                Text(text = "${item.product.title} (x${item.quantity}) - $${item.subtotal}")
            }
        }
    }
}