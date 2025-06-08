package com.example.nevil_watch.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nevil_watch.api.IpInfoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ShippingAddress(
    val id: String = "",
    val fullName: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = "",
    val countryCode: String = "",
    val phoneNumber: String = "",
    val additionalInstructions: String = "",
    val isDefault: Boolean = false
)

sealed class ShippingAddressUiState {
    object Loading : ShippingAddressUiState()
    data class Success(val address: ShippingAddress) : ShippingAddressUiState()
    data class Error(val message: String) : ShippingAddressUiState()
}

class ShippingAddressViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ShippingAddressUiState>(
        ShippingAddressUiState.Success(
            ShippingAddress(
                fullName = "",
                streetAddress = "",
                city = "",
                state = "",
                postalCode = "",
                country = "",
                countryCode = "",
                phoneNumber = "",
                additionalInstructions = ""
            )
        )
    )
    val uiState: StateFlow<ShippingAddressUiState> = _uiState.asStateFlow()

    // Form fields
    var fullName by mutableStateOf("")
        private set
    var streetAddress by mutableStateOf("")
        private set
    var city by mutableStateOf("")
        private set
    var state by mutableStateOf("")
        private set
    var postalCode by mutableStateOf("")
        private set
    var country by mutableStateOf("")
        private set
    var countryCode by mutableStateOf("")
        private set
    var phoneNumber by mutableStateOf("")
        private set
    var additionalInstructions by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // init {
    //     fetchLocationData()
    // }

    fun fetchLocationData() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = withContext(Dispatchers.IO) {
                    IpInfoApi.service.getIpInfo("8.8.8.8") // Using Google's DNS as example
                }
                
                // Update form fields with location data
                city = response.city
                state = response.region
                postalCode = response.postal
                country = response.country
                
                // Extract country code from org field (usually contains country code)
                countryCode = response.org.split(" ").firstOrNull() ?: ""
                
            } catch (e: Exception) {
                error = "Failed to fetch location data: ${e.message}"
            } finally {
                isLoading = false
                // Always update the UI state so the form can show
                _uiState.value = ShippingAddressUiState.Success(
                    ShippingAddress(
                        fullName = fullName,
                        streetAddress = streetAddress,
                        city = city,
                        state = state,
                        postalCode = postalCode,
                        country = country,
                        countryCode = countryCode,
                        phoneNumber = phoneNumber,
                        additionalInstructions = additionalInstructions
                    )
                )
            }
        }
    }

    // Form validation
    val isFormValid: Boolean
        get() = fullName.isNotBlank() &&
                streetAddress.isNotBlank() &&
                city.isNotBlank() &&
                state.isNotBlank() &&
                postalCode.isNotBlank() &&
                country.isNotBlank() &&
                countryCode.isNotBlank() &&
                phoneNumber.isNotBlank()

    // Update functions
    fun updateFullName(name: String) {
        fullName = name
    }

    fun updateStreetAddress(address: String) {
        streetAddress = address
    }

    fun updateCity(cityName: String) {
        city = cityName
    }

    fun updateState(stateName: String) {
        state = stateName
    }

    fun updatePostalCode(code: String) {
        postalCode = code
    }

    fun updateCountry(name: String, code: String) {
        country = name
        countryCode = code
    }

    fun updatePhoneNumber(number: String) {
        phoneNumber = number
    }

    fun updateAdditionalInstructions(instructions: String) {
        additionalInstructions = instructions
    }

    fun saveAddress() {
        viewModelScope.launch {
            try {
                val address = ShippingAddress(
                    fullName = fullName,
                    streetAddress = streetAddress,
                    city = city,
                    state = state,
                    postalCode = postalCode,
                    country = country,
                    countryCode = countryCode,
                    phoneNumber = phoneNumber,
                    additionalInstructions = additionalInstructions
                )
                
                // TODO: Implement API call to save address
                // For now, just update the UI state
                _uiState.value = ShippingAddressUiState.Success(address)
            } catch (e: Exception) {
                _uiState.value = ShippingAddressUiState.Error(e.message ?: "Failed to save address")
            }
        }
    }

    fun loadAddress(addressId: String) {
        viewModelScope.launch {
            try {
                // TODO: Implement API call to load address
                // For now, just set loading state
                _uiState.value = ShippingAddressUiState.Loading
            } catch (e: Exception) {
                _uiState.value = ShippingAddressUiState.Error(e.message ?: "Failed to load address")
            }
        }
    }
} 