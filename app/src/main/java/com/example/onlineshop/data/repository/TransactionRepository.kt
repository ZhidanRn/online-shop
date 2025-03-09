package com.example.onlineshop.data.repository

import com.example.onlineshop.data.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TransactionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val transactions = mutableListOf<Transaction>()

    suspend fun placeOrder(transaction: Transaction): Boolean {
        return try {
            db.collection("transactions")
                .add(transaction)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getTransactions(): Flow<List<Transaction>> = callbackFlow {
        val listener = db.collection("transactions")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val transactions = snapshot?.toObjects(Transaction::class.java) ?: emptyList()
                trySend(transactions)
            }
        awaitClose { listener.remove() }
    }


}
