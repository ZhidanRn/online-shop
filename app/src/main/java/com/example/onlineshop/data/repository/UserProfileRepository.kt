package com.example.onlineshop.data.repository

import android.net.Uri
import com.example.onlineshop.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserProfileRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun getCurrentUserData(): User? {
        val userId = auth.currentUser?.uid ?: return null
        val snapshot = firestore.collection("users").document(userId).get().await()
        return snapshot.toObject(User::class.java)
    }

    suspend fun updateUserProfile(user: User) {
        val userId = auth.currentUser?.uid ?: return
        val updates = mutableMapOf<String, Any>()

        if (user.name.isNotEmpty()) updates["name"] = user.name
        if (user.email.isNotEmpty()) updates["email"] = user.email
        if (user.phone.isNotEmpty()) updates["phone"] = user.phone
        if (user.address.isNotEmpty()) updates["address"] = user.address

        if (updates.isNotEmpty()) {
            firestore.collection("users").document(userId).update(updates).await()
        }
    }

    suspend fun updateProfilePicture(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .update("profileImageUrl", uri)
            .await()
    }
}
