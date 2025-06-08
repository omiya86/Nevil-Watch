package com.example.nevil_watch.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import com.example.nevil_watch.R

@Composable
fun AmbientLightStatus() {
    val context = LocalContext.current
    var lightLevel by remember { mutableStateOf(0f) }
    var sensorManager by remember { mutableStateOf<SensorManager?>(null) }
    var lightSensor by remember { mutableStateOf<Sensor?>(null) }

    DisposableEffect(context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    lightLevel = event.values[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not needed for light sensor
            }
        }

        lightSensor?.let { sensor ->
            sensorManager?.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        onDispose {
            sensorManager?.unregisterListener(sensorEventListener)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Image(
            painter = painterResource(
                id = if (lightLevel > 100) R.drawable.sunny 
                     else R.drawable.lightmode
            ),
            contentDescription = "Ambient light level",
            modifier = Modifier.size(24.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = when {
                lightLevel > 100 -> "Bright"
                lightLevel > 50 -> "Normal"
                else -> "Dark"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
} 