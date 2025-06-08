package com.example.nevil_watch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nevil_watch.model.CartItem
import com.example.nevil_watch.model.Watch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class CartUiState {
    object Loading : CartUiState()
    data class Success(val items: List<CartItem>) : CartUiState()
    data class Error(val message: String) : CartUiState()
}

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cartRef = database.getReference("carts")

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        val userId = auth.currentUser?.uid ?: return
        
        cartRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { 
                    it.getValue(CartItem::class.java)?.apply {
                        imageResId = getDrawableResourceForWatch(watchImage)
                    }
                }
                _uiState.value = CartUiState.Success(items)
                calculateTotal(items)
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.value = CartUiState.Error(error.message)
            }
        })
    }

    fun addToCart(watch: Watch) {
        val userId = auth.currentUser?.uid ?: return
        val cartItemId = UUID.randomUUID().toString()

        val cartItem = CartItem(
            id = cartItemId,
            watchId = watch.id,
            userId = userId,
            quantity = 1,
            price = watch.price,
            watchName = watch.name,
            watchImage = watch.id // Using watch ID as image reference
        )

        cartRef.child(userId).child(cartItemId).setValue(cartItem)
            .addOnFailureListener { exception ->
                _uiState.value = CartUiState.Error("Failed to add item: ${exception.message}")
            }
    }

    fun removeFromCart(cartItem: CartItem) {
        val userId = auth.currentUser?.uid ?: return
        cartRef.child(userId).child(cartItem.id).removeValue()
            .addOnFailureListener { exception ->
                _uiState.value = CartUiState.Error("Failed to remove item: ${exception.message}")
            }
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity < 1) return
        val userId = auth.currentUser?.uid ?: return
        
        cartRef.child(userId).child(cartItem.id)
            .child("quantity").setValue(newQuantity)
            .addOnFailureListener { exception ->
                _uiState.value = CartUiState.Error("Failed to update quantity: ${exception.message}")
            }
    }

    private fun calculateTotal(items: List<CartItem>) {
        val total = items.sumOf { it.price * it.quantity }
        _totalPrice.value = total
    }

    fun clearCart() {
        val userId = auth.currentUser?.uid ?: return
        cartRef.child(userId).removeValue()
            .addOnFailureListener { exception ->
                _uiState.value = CartUiState.Error("Failed to clear cart: ${exception.message}")
            }
    }

    private fun getDrawableResourceForWatch(watchId: String): Int {
        return try {
            val resourceName = watchId.lowercase()
            val resources = getApplication<Application>().resources
            resources.getIdentifier(resourceName, "drawable", getApplication<Application>().packageName)
        } catch (e: Exception) {
            0 // Return 0 to indicate no resource found
        }
    }
} 