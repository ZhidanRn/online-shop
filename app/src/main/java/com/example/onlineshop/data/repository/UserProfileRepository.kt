package com.example.onlineshop.data.repository

import com.example.onlineshop.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserProfileRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun getCurrentUserData(): UserProfile? {
        val userId = auth.currentUser?.uid ?: return null
        val snapshot = firestore.collection("users").document(userId).get().await()
        return snapshot.toObject(UserProfile::class.java)
    }

    suspend fun updateUserProfile(userProfile: UserProfile) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).set(userProfile).await()
    }
}
