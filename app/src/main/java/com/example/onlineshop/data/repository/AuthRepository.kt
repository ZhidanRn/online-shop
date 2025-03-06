package com.example.onlineshop.data.repository

import com.example.onlineshop.data.model.User
import com.example.onlineshop.data.lib.DataStoreManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerUser(email: String, password: String, role: String, onComplete: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onComplete(false, "Email and password cannot be empty")
            return
        }

        try {
            // Validasi email sederhana
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                onComplete(false, "Format email not valid")
                return
            }

            // Validasi kekuatan password
            if (password.length < 6) {
                onComplete(false, "Password minimal 6 characters")
                return
            }

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid

            userId?.let {
                val userMap = hashMapOf(
                    "email" to email,
                    "role" to role,
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("users").document(userId).set(userMap).await()
                onComplete(true, "Registration successful")
            } ?: onComplete(false, "Failed to create user")
        } catch (e: FirebaseAuthUserCollisionException) {
            onComplete(false, "Email already exists please login")
        } catch (e: Exception) {
            onComplete(false, e.localizedMessage ?: "Registrasi failed")
        }
    }

    suspend fun loginUser(email: String, password: String, onComplete: (Boolean, String?, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onComplete(false, null, "Email and password cannot be empty")
            return
        }

        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid

            userId?.let {
                val doc = db.collection("users").document(userId).get().await()
                val role = doc.getString("role")
                onComplete(true, role, "Login successful")
            } ?: onComplete(false, null, "User not found")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            onComplete(false, null, "Email or password is incorrect")
        } catch (e: Exception) {
            onComplete(false, null, e.localizedMessage ?: "Login failed")
        }
    }

    fun logout(dataStoreManager: DataStoreManager) {
        auth.signOut()

        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.clearSession()
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

//    fun getCurrentUser(): Flow<User?> = callbackFlow {
//        val listener = FirebaseAuth.getInstance().addAuthStateListener { auth ->
//            val firebaseUser = auth.currentUser
//            if (firebaseUser != null) {
//                trySend(
//                    User(
//                        uid = firebaseUser.uid,
//                        email = firebaseUser.email ?: "",
//                        role = "user"
//                    )
//                )
//            } else {
//                trySend(null)
//            }
//        }
//
//        awaitClose { FirebaseAuth.getInstance().removeAuthStateListener(listener) }
//    }
}