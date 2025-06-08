package com.example.nevil_watch.model

import com.google.firebase.database.PropertyName

data class PaymentMethod(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("cardNumber")
    @set:PropertyName("cardNumber")
    var cardNumber: String = "",

    @get:PropertyName("cardHolderName")
    @set:PropertyName("cardHolderName")
    var cardHolderName: String = "",

    @get:PropertyName("expiryDate")
    @set:PropertyName("expiryDate")
    var expiryDate: String = "",

    @get:PropertyName("isDefault")
    @set:PropertyName("isDefault")
    var isDefault: Boolean = false,

    @get:PropertyName("cardType")
    @set:PropertyName("cardType")
    var cardType: String = "" // visa, mastercard, etc.
) 