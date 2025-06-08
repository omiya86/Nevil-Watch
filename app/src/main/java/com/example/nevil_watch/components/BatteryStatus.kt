package com.example.nevil_watch.components

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.nevil_watch.R

@Composable
fun BatteryStatus() {
    val context = LocalContext.current
    var batteryPercentage by remember { mutableStateOf(getBatteryPercentage(context)) }
    var isCharging by remember { mutableStateOf(isCharging(context)) }

    // Update battery status every 30 seconds
    LaunchedEffect(Unit) {
        while (true) {
            batteryPercentage = getBatteryPercentage(context)
            isCharging = isCharging(context)
            delay(30000) // 30 seconds delay
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Image(
            painter = painterResource(
                id = if (batteryPercentage > 15) R.drawable.battery_full 
                    else R.drawable.battery_alert
            ),
            contentDescription = "Battery status",
            modifier = Modifier.size(24.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                if (batteryPercentage > 15) MaterialTheme.colorScheme.onSurface 
                else MaterialTheme.colorScheme.error
            )
        )
        Text(
            text = "$batteryPercentage%",
            style = MaterialTheme.typography.bodyMedium,
            color = if (batteryPercentage > 15) MaterialTheme.colorScheme.onSurface 
                   else MaterialTheme.colorScheme.error
        )
    }
}

private fun getBatteryPercentage(context: Context): Int {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }
    
    val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    
    return if (level != -1 && scale != -1) {
        (level * 100 / scale.toFloat()).toInt()
    } else {
        0
    }
}

private fun isCharging(context: Context): Boolean {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }
    
    val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    return status == BatteryManager.BATTERY_STATUS_CHARGING || 
           status == BatteryManager.BATTERY_STATUS_FULL
} 