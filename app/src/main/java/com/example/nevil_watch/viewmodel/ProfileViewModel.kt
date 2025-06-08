package com.example.nevil_watch.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    var userName by mutableStateOf(auth.currentUser?.displayName ?: "Guest User")
        private set
        
    var userEmail by mutableStateOf(auth.currentUser?.email ?: "")
        private set

    init {
        refreshUserProfile()
    }

    private fun refreshUserProfile() {
        viewModelScope.launch {
            try {
                // Reload the user data to get the latest information
                auth.currentUser?.reload()?.await()
                userName = auth.currentUser?.displayName ?: "Guest User"
                userEmail = auth.currentUser?.email ?: ""
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
} 