package com.example.nevil_watch.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object Categories : BottomNavItem(
        route = "categories",
        title = "Categories",
        icon = Icons.Default.List
    )
    object Cart : BottomNavItem(
        route = "cart",
        title = "Cart",
        icon = Icons.Default.ShoppingCart
    )
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )

    companion object {
        val items = listOf(Home, Categories, Cart, Profile)
    }
} 