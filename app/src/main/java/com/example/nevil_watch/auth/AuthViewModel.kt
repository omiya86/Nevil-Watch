package com.example.nevil_watch.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nevil_watch.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val userProfileViewModel = UserProfileViewModel(application)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    init {
        // Clear any previous authentication state
        auth.signOut()
        _authState.value = AuthState.Initial
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Starting sign in process for email: $email")
                
                val result = auth.signInWithEmailAndPassword(email, password).await()
                Log.d("AuthViewModel", "Firebase authentication successful")
                
                // Get user data from database
                val userRef = database.getReference("users")
                    .child(result.user?.uid ?: "")
                Log.d("AuthViewModel", "Fetching user data from path: ${userRef.path}")
                
                val userData = userRef.get().await()
                    .getValue(object : GenericTypeIndicator<HashMap<String, String>>() {})
                Log.d("AuthViewModel", "Raw user data from Firebase: $userData")

                val userName = userData?.get("name") ?: result.user?.displayName ?: email.substringBefore('@')
                val contactNumber = userData?.get("contactNumber")
                
                Log.d("AuthViewModel", "Extracted user info - Name: $userName, Contact: $contactNumber")
                
                // Save user profile data locally
                if (userName != null) {
                    Log.d("AuthViewModel", "Saving user profile to local storage...")
                    userProfileViewModel.saveUserProfile(
                        name = userName,
                        contactNumber = contactNumber ?: ""
                    )
                    Log.d("AuthViewModel", "User profile saved locally")
                } else {
                    Log.w("AuthViewModel", "No user name available, skipping local storage")
                }
                
                _authState.value = AuthState.Success("Welcome back, $userName!")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign in failed", e)
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    fun register(name: String, email: String, password: String, contactNumber: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Starting registration for email: $email")
                
                // Create user with email and password
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                
                // Update the user's display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                
                result.user?.updateProfile(profileUpdates)?.await()
                
                Log.d("AuthViewModel", "User created and profile updated, saving to database...")
                
                // Store user information in the database
                val userRef = database.getReference("users")
                    .child(result.user?.uid ?: throw Exception("User ID not found"))
                
                val userData = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "contactNumber" to contactNumber
                )
                
                userRef.setValue(userData).await()
                Log.d("AuthViewModel", "User data saved to database")
                
                // Save to local storage
                userProfileViewModel.saveUserProfile(name, contactNumber)
                Log.d("AuthViewModel", "User profile saved locally")
                
                _authState.value = AuthState.Success("Successfully registered!")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e)
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Initial
    }

    fun isUserSignedIn(): Boolean = auth.currentUser != null
} 