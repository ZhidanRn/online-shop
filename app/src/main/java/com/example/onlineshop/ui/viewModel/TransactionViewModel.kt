package com.example.onlineshop.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.model.CartItem
import com.example.onlineshop.data.model.Transaction
import com.example.onlineshop.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransactionViewModel(private val repository: TransactionRepository = TransactionRepository()) : ViewModel() {
    private val _isOrderPlaced = MutableStateFlow(false)
    val isOrderPlaced: StateFlow<Boolean> = _isOrderPlaced
    val cartViewModel: CartViewModel = CartViewModel()
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    init {
        viewModelScope.launch {
            repository.getTransactions().collect { transactionList ->
                Log.d("TransactionViewModel", "Transactions: $transactionList")
                _transactions.value = transactionList
            }
        }
    }

    fun placeOrder(cartItems: List<CartItem>, totalPrice: Double) {
        viewModelScope.launch {
            val order = Transaction(
                items = cartItems,
                totalPrice = totalPrice
            )

            val success = repository.placeOrder(order)
            _isOrderPlaced.value = success

            cartViewModel.clearCart()

        }
    }
}
