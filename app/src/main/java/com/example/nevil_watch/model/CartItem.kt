package com.example.nevil_watch.model

import com.google.firebase.database.PropertyName

data class CartItem(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("watchId")
    @set:PropertyName("watchId")
    var watchId: String = "",

    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("quantity")
    @set:PropertyName("quantity")
    var quantity: Int = 1,

    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: Double = 0.0,

    @get:PropertyName("watchName")
    @set:PropertyName("watchName")
    var watchName: String = "",

    @get:PropertyName("watchImage")
    @set:PropertyName("watchImage")
    var watchImage: String = "",

    // This will not be stored in Firebase
    var imageResId: Int = 0
) 