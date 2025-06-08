package com.example.nevil_watch.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import com.example.nevil_watch.R

data class NetworkState(
    val isConnected: Boolean = false,
    val isCellular: Boolean = false,
    val isWifi: Boolean = false
)

@Composable
fun NetworkStatus() {
    val context = LocalContext.current
    var networkState by remember { mutableStateOf(NetworkState()) }

    DisposableEffect(context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateNetworkState(connectivityManager, networkState) { networkState = it }
            }

            override fun onLost(network: Network) {
                networkState = NetworkState(isConnected = false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                updateNetworkState(connectivityManager, networkState) { networkState = it }
            }
        }

        // Initial state
        updateNetworkState(connectivityManager, networkState) { networkState = it }

        // Register callback
        connectivityManager.registerDefaultNetworkCallback(callback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        when {
            !networkState.isConnected -> {
                Image(
                    painter = painterResource(id = R.drawable.wifioff),
                    contentDescription = "No network connection",
                    modifier = Modifier.size(24.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.error
                    )
                )
                Text(
                    text = "Offline",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            networkState.isWifi -> {
                Image(
                    painter = painterResource(id = R.drawable.wififull),
                    contentDescription = "WiFi connection",
                    modifier = Modifier.size(24.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "WiFi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            networkState.isCellular -> {
                Image(
                    painter = painterResource(id = R.drawable.signalfourbar),
                    contentDescription = "Cellular connection",
                    modifier = Modifier.size(24.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Mobile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun updateNetworkState(
    connectivityManager: ConnectivityManager,
    currentState: NetworkState,
    updateState: (NetworkState) -> Unit
) {
    val activeNetwork = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
    
    if (capabilities == null) {
        updateState(NetworkState(isConnected = false))
        return
    }

    val isConnected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                     capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

    updateState(NetworkState(
        isConnected = isConnected,
        isWifi = isWifi,
        isCellular = isCellular
    ))
} 