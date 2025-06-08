package com.example.nevil_watch.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nevil_watch.R
import com.example.nevil_watch.viewmodel.ShippingAddressUiState
import com.example.nevil_watch.viewmodel.ShippingAddressViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Country(val name: String, val code: String)

fun loadCountriesFromAssets(context: Context): List<Country> {
    val jsonString = context.assets.open("countries.json").bufferedReader().use { it.readText() }
    val listType = object : TypeToken<List<Country>>() {}.type
    return Gson().fromJson(jsonString, listType)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressScreen(
    viewModel: ShippingAddressViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showCountryPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shipping Address") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ShippingAddressUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ShippingAddressUiState.Error -> {
                    val errorMessage = (uiState as ShippingAddressUiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchLocationData() }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Country Selection
                        OutlinedTextField(
                            value = viewModel.country,
                            onValueChange = { },
                            label = { Text("Country") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showCountryPicker = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Country")
                                }
                            }
                        )

                        // Country Code (Read-only, set by country selection)
                        OutlinedTextField(
                            value = viewModel.countryCode,
                            onValueChange = { },
                            label = { Text("Country Code") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        // Full Name
                        OutlinedTextField(
                            value = viewModel.fullName,
                            onValueChange = { viewModel.updateFullName(it) },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Street Address
                        OutlinedTextField(
                            value = viewModel.streetAddress,
                            onValueChange = { viewModel.updateStreetAddress(it) },
                            label = { Text("Street Address") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // City
                        OutlinedTextField(
                            value = viewModel.city,
                            onValueChange = { viewModel.updateCity(it) },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // State/Province
                        OutlinedTextField(
                            value = viewModel.state,
                            onValueChange = { viewModel.updateState(it) },
                            label = { Text("State/Province") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Postal Code
                        OutlinedTextField(
                            value = viewModel.postalCode,
                            onValueChange = { viewModel.updatePostalCode(it) },
                            label = { Text("Postal Code") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Phone Number
                        OutlinedTextField(
                            value = viewModel.phoneNumber,
                            onValueChange = { viewModel.updatePhoneNumber(it) },
                            label = { Text("Phone Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Additional Instructions
                        OutlinedTextField(
                            value = viewModel.additionalInstructions,
                            onValueChange = { viewModel.updateAdditionalInstructions(it) },
                            label = { Text("Additional Instructions (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        // Save Address Button
                        Button(
                            onClick = { viewModel.saveAddress() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            enabled = viewModel.isFormValid
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.save),
                                    contentDescription = "Save Address",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Address")
                            }
                        }
                    }
                }
            }
        }
    }

    // Country Picker Dialog
    if (showCountryPicker) {
        CountryPickerDialog(
            onDismiss = { showCountryPicker = false },
            onCountrySelected = { name, code ->
                viewModel.updateCountry(name, code)
                showCountryPicker = false
            }
        )
    }
}

@Composable
private fun CountryPickerDialog(
    onDismiss: () -> Unit,
    onCountrySelected: (String, String) -> Unit
) {
    val context = LocalContext.current
    val countries = remember { loadCountriesFromAssets(context) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Country") },
        text = {
            LazyColumn {
                items(countries) { country ->
                    Text(
                        text = "${country.name} (${country.code})",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCountrySelected(country.name, country.code) }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 