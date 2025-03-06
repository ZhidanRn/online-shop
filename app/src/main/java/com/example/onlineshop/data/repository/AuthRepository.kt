package com.example.onlineshop.data.repository

import android.util.Log
import com.example.onlineshop.data.lib.DataStoreManager
import com.example.onlineshop.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerUser(email: String, password: String, name: String, phone: String, address: String, role: String, profileImageUrl: String, onComplete: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onComplete(false, "Email and password cannot be empty")
            return
        }

        try {
            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()

            val result = auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword).await()
            val userId = result.user?.uid

            userId?.let {
                val user = User(
                    uid = userId,
                    name = name,
                    email = trimmedEmail,
                    phone = phone,
                    address = address,
                    role = role,
                    profileImageUrl = profileImageUrl
                )
                Log.d("User registered: ", user.toString())
                db.collection("users").document(userId).set(user).await()

                onComplete(true, "Registration successful")
            } ?: onComplete(false, "Failed to create user")
        } catch (e: FirebaseAuthUserCollisionException) {
            onComplete(false, "Email already exists, please login")
        } catch (e: Exception) {
            onComplete(false, e.localizedMessage ?: "Registration failed")
        }
    }


    suspend fun loginUser(email: String, password: String, onComplete: (Boolean, User?, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onComplete(false, null, "Email and password cannot be empty")
            return
        }

        try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
            val userId = result.user?.uid

            userId?.let {
                val doc = db.collection("users").document(userId).get().await()
                val user = doc.toObject(User::class.java)

                if (user != null) {
                    onComplete(true, user, "Login successful")
                } else {
                    onComplete(false, null, "User data not found")
                }
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
}
