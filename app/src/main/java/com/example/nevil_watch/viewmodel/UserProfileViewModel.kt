package com.example.nevil_watch.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nevil_watch.data.LocalUserDataSource
import com.example.nevil_watch.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val localUserDataSource = LocalUserDataSource(application)
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                Log.d("UserProfileViewModel", "Loading user profile from local storage...")
                val profile = localUserDataSource.getUserProfile()
                Log.d("UserProfileViewModel", "Loaded profile: $profile")
                _userProfile.value = profile
                
                // Debug log the current state
                Log.d("UserProfileViewModel", "Current profile state - Name: ${profile?.name}, Contact: ${profile?.contactNumber}")
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error loading user profile", e)
            }
        }
    }

    fun saveUserProfile(name: String, contactNumber: String) {
        viewModelScope.launch {
            try {
                Log.d("UserProfileViewModel", "Saving user profile - Name: $name, Contact: $contactNumber")
                val userProfile = UserProfile(name, contactNumber)
                localUserDataSource.saveUserProfile(userProfile)
                _userProfile.value = userProfile
                Log.d("UserProfileViewModel", "User profile saved successfully")
                
                // Verify the save by reloading
                loadUserProfile()
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error saving user profile", e)
            }
        }
    }

    fun clearUserProfile() {
        viewModelScope.launch {
            try {
                Log.d("UserProfileViewModel", "Clearing user profile...")
                localUserDataSource.clearUserProfile()
                _userProfile.value = null
                Log.d("UserProfileViewModel", "User profile cleared successfully")
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error clearing user profile", e)
            }
        }
    }
} 