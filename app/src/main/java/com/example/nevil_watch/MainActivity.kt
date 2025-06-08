package com.example.nevil_watch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nevil_watch.navigation.AuthNavigation
import com.example.nevil_watch.screens.MainScreen
import com.example.nevil_watch.screens.NoInternetScreen
import com.example.nevil_watch.ui.theme.NevilWatchTheme
import com.example.nevil_watch.viewmodel.NetworkConnectivityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NevilWatchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val networkViewModel: NetworkConnectivityViewModel = viewModel()
                    val isConnected by networkViewModel.isConnected.collectAsState()
                    var showAuth by remember { mutableStateOf(true) }

                    if (!isConnected) {
                        NoInternetScreen(
                            onRetryClick = {
                                networkViewModel.checkConnectivity()
                            }
                        )
                    } else {
                        if (showAuth) {
                            AuthNavigation(
                                onAuthSuccess = {
                                    showAuth = false
                                }
                            )
                        } else {
                            MainScreen(
                                onSignOut = {
                                    showAuth = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}