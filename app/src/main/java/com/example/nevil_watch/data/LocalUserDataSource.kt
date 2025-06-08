package com.example.nevil_watch.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalUserDataSource(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    suspend fun saveUserProfile(userProfile: UserProfile) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("LocalUserDataSource", "Saving user profile to SharedPreferences...")
                sharedPreferences.edit().apply {
                    putString(KEY_NAME, userProfile.name)
                    putString(KEY_CONTACT_NUMBER, userProfile.contactNumber)
                    apply()
                }
                Log.d("LocalUserDataSource", "User profile saved successfully")
            } catch (e: Exception) {
                Log.e("LocalUserDataSource", "Error saving user profile", e)
                throw e
            }
        }
    }

    suspend fun getUserProfile(): UserProfile? = withContext(Dispatchers.IO) {
        try {
            Log.d("LocalUserDataSource", "Getting user profile from SharedPreferences...")
            val name = sharedPreferences.getString(KEY_NAME, null)
            val contactNumber = sharedPreferences.getString(KEY_CONTACT_NUMBER, null)
            
            Log.d("LocalUserDataSource", "Retrieved values - Name: $name, Contact: $contactNumber")
            
            if (name != null && contactNumber != null) {
                UserProfile(name, contactNumber).also {
                    Log.d("LocalUserDataSource", "Returning user profile: $it")
                }
            } else {
                Log.d("LocalUserDataSource", "No user profile found in SharedPreferences")
                null
            }
        } catch (e: Exception) {
            Log.e("LocalUserDataSource", "Error getting user profile", e)
            throw e
        }
    }

    suspend fun clearUserProfile() = withContext(Dispatchers.IO) {
        try {
            Log.d("LocalUserDataSource", "Clearing user profile from SharedPreferences...")
            sharedPreferences.edit().clear().apply()
            Log.d("LocalUserDataSource", "User profile cleared successfully")
        } catch (e: Exception) {
            Log.e("LocalUserDataSource", "Error clearing user profile", e)
            throw e
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "user_profile_prefs"
        private const val KEY_NAME = "name"
        private const val KEY_CONTACT_NUMBER = "contact_number"
    }
} 