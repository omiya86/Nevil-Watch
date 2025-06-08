package com.example.nevil_watch.viewmodel

import android.app.Application
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nevil_watch.R
import com.example.nevil_watch.data.WatchRepository
import com.example.nevil_watch.data.WatchRepositoryImpl
import com.example.nevil_watch.model.Watch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

sealed class WatchesUiState {
    object Loading : WatchesUiState()
    data class Success(val watches: List<Watch>) : WatchesUiState()
    data class Error(val message: String) : WatchesUiState()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WatchRepository = WatchRepositoryImpl(application)
    private val database = FirebaseDatabase.getInstance()
    private val watchesRef = database.getReference("watches")
    private val _uiState = MutableStateFlow<WatchesUiState>(WatchesUiState.Loading)
    val uiState: StateFlow<WatchesUiState> = _uiState
    private val resources: Resources = application.resources

    private val _selectedWatch = MutableStateFlow<Watch?>(null)
    val selectedWatch: StateFlow<Watch?> = _selectedWatch

    init {
        loadWatches()
    }

    fun loadWatches() {
        watchesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val watches = snapshot.children.mapNotNull { 
                    it.getValue(Watch::class.java)?.apply {
                        imageResId = getDrawableResourceForWatch(id)
                    }
                }
                _uiState.value = WatchesUiState.Success(watches)
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.value = WatchesUiState.Error(error.message)
            }
        })
    }

    fun loadWatchesByCategory(categoryId: String) {
        watchesRef.orderByChild("category")
            .equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val watches = snapshot.children.mapNotNull { 
                        it.getValue(Watch::class.java)?.apply {
                            imageResId = getDrawableResourceForWatch(id)
                        }
                    }.filter { watch ->
                        watch.brand == "Nevil sport"
                    }
                    _uiState.value = if (watches.isEmpty()) {
                        WatchesUiState.Error("No watches found in this category")
                    } else {
                        WatchesUiState.Success(watches)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.value = WatchesUiState.Error(error.message)
                }
            })
    }

    fun loadWatchById(watchId: String) {
        watchesRef.child(watchId).get().addOnSuccessListener { snapshot ->
            snapshot.getValue(Watch::class.java)?.let { watch ->
                watch.imageResId = getDrawableResourceForWatch(watch.id)
                _selectedWatch.value = watch
            }
        }
    }

    fun getWatchById(watchId: String?): Watch? {
        return when (val currentState = _uiState.value) {
            is WatchesUiState.Success -> {
                currentState.watches.find { it.id == watchId }
            }
            else -> null
        }
    }

    private fun getDrawableResourceForWatch(watchId: String): Int {
        return try {
            val resourceName = watchId.lowercase()
            resources.getIdentifier(resourceName, "drawable", getApplication<Application>().packageName)
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error getting drawable resource for watch $watchId", e)
            0 // Return 0 to indicate no resource found
        }
    }

    fun refreshWatches() {
        _uiState.value = WatchesUiState.Loading
        loadWatches()
    }
} 