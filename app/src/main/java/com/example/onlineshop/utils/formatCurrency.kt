package com.example.onlineshop.utils

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("usd", "US"))
    return format.format(amount)
}