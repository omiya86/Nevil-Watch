package com.example.nevil_watch.model

import com.google.firebase.database.PropertyName

data class Watch(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("brand")
    @set:PropertyName("brand")
    var brand: String = "",

    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: Double = 0.0,

    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("movement")
    @set:PropertyName("movement")
    var movement: String = "",

    @get:PropertyName("waterResistance")
    @set:PropertyName("waterResistance")
    var waterResistance: String = "",

    @get:PropertyName("powerReserve")
    @set:PropertyName("powerReserve")
    var powerReserve: String = "",

    @get:PropertyName("caseMaterial")
    @set:PropertyName("caseMaterial")
    var caseMaterial: String = "",

    // This will not be stored in Firebase
    var imageResId: Int = 0
) 