package com.example.nevil_watch.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nevil_watch.R
import com.example.nevil_watch.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class CategoriesUiState {
    object Loading : CategoriesUiState()
    data class Success(val categories: List<Category>) : CategoriesUiState()
    data class Error(val message: String) : CategoriesUiState()
}

class CategoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading)
    val uiState: StateFlow<CategoriesUiState> = _uiState

    init {
        loadCategories()
    }

    private fun loadCategories() {
        // For now, we'll use hardcoded categories. Later this can be replaced with Firebase data
        val categories = listOf(
            Category(
                id = "luxury",
                name = "Luxury Watches",
                description = "Premium timepieces for the discerning collector",
                imageResId = R.drawable.luxurywatches
            ),
            Category(
                id = "sport",
                name = "Sport Watches",
                description = "Durable watches for active lifestyles",
                imageResId = R.drawable.sportswatches
            ),
            Category(
                id = "smart",
                name = "Smart Watches",
                description = "Connected watches with modern features",
                imageResId = R.drawable.smartwatches
            ),
            Category(
                id = "classic",
                name = "Classic Watches",
                description = "Timeless designs for everyday wear",
                imageResId = R.drawable.classicwatches
            ),
            Category(
                id = "fashion",
                name = "Fashion Watches",
                description = "Stylish watches to complement your look",
                imageResId = R.drawable.fashionwatches
            )
        )
        _uiState.value = CategoriesUiState.Success(categories)
    }
} 