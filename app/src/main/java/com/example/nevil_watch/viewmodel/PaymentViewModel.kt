package com.example.nevil_watch.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nevil_watch.model.PaymentMethod
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

sealed class PaymentUiState {
    object Loading : PaymentUiState()
    data class Success(val paymentMethods: List<PaymentMethod>) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

class PaymentViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val paymentRef = database.getReference("payments")

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState

    init {
        loadPaymentMethods()
    }

    private fun loadPaymentMethods() {
        val userId = auth.currentUser?.uid ?: return
        
        paymentRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val paymentMethods = snapshot.children.mapNotNull { 
                    it.getValue(PaymentMethod::class.java)
                }
                _uiState.value = PaymentUiState.Success(paymentMethods)
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.value = PaymentUiState.Error(error.message)
            }
        })
    }

    fun addPaymentMethod(
        cardNumber: String,
        cardHolderName: String,
        expiryDate: String,
        cardType: String
    ) {
        val userId = auth.currentUser?.uid ?: run {
            _uiState.value = PaymentUiState.Error("User not authenticated")
            return
        }
        val paymentId = UUID.randomUUID().toString()

        val paymentMethod = PaymentMethod(
            id = paymentId,
            userId = userId,
            cardNumber = cardNumber,
            cardHolderName = cardHolderName,
            expiryDate = expiryDate,
            cardType = cardType,
            isDefault = false
        )

        _uiState.value = PaymentUiState.Loading
        paymentRef.child(userId).child(paymentId).setValue(paymentMethod)
            .addOnSuccessListener {
                loadPaymentMethods() // Refresh the payment methods list
            }
            .addOnFailureListener { exception ->
                _uiState.value = PaymentUiState.Error("Failed to add payment method: ${exception.message}")
            }
    }

    fun removePaymentMethod(paymentId: String) {
        val userId = auth.currentUser?.uid ?: return
        paymentRef.child(userId).child(paymentId).removeValue()
            .addOnFailureListener { exception ->
                _uiState.value = PaymentUiState.Error("Failed to remove payment method: ${exception.message}")
            }
    }

    fun setDefaultPaymentMethod(paymentId: String) {
        val userId = auth.currentUser?.uid ?: return
        
        paymentRef.child(userId).get().addOnSuccessListener { snapshot ->
            // First, set all payment methods to non-default
            snapshot.children.forEach { paymentSnapshot ->
                paymentRef.child(userId)
                    .child(paymentSnapshot.key!!)
                    .child("isDefault")
                    .setValue(false)
            }
            
            // Then set the selected payment method as default
            paymentRef.child(userId)
                .child(paymentId)
                .child("isDefault")
                .setValue(true)
        }
    }
} 