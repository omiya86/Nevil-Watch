package com.example.nevil_watch.data

import android.content.Context
import com.example.nevil_watch.model.Watch
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface WatchRepository {
    fun getWatches(): Flow<List<Watch>>
    fun getWatchById(id: String): Flow<Watch?>
    suspend fun saveWatchesLocally(watches: List<Watch>)
    suspend fun loadWatchesFromLocal(): List<Watch>
}

class WatchRepositoryImpl(private val context: Context) : WatchRepository {
    private val localFileName = "local_watches.json"
    private val onlineJsonUrl = "https://raw.githubusercontent.com/yourusername/nevil-watch/main/watches.json"
    private val database = FirebaseDatabase.getInstance()
    private val watchesRef = database.getReference("watches")

    override fun getWatches(): Flow<List<Watch>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val watches = snapshot.children.mapNotNull { it.getValue(Watch::class.java) }
                trySend(watches)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }

        watchesRef.addValueEventListener(listener)
        awaitClose { watchesRef.removeEventListener(listener) }
    }

    override fun getWatchById(id: String): Flow<Watch?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val watch = snapshot.getValue(Watch::class.java)
                trySend(watch)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }

        watchesRef.child(id).addValueEventListener(listener)
        awaitClose { watchesRef.child(id).removeEventListener(listener) }
    }

    override suspend fun saveWatchesLocally(watches: List<Watch>) {
        val jsonArray = watches.map { watch ->
            JSONObject().apply {
                put("id", watch.id)
                put("name", watch.name)
                put("brand", watch.brand)
                put("price", watch.price)
                put("description", watch.description)
                put("movement", watch.movement)
                put("waterResistance", watch.waterResistance)
                put("powerReserve", watch.powerReserve)
                put("caseMaterial", watch.caseMaterial)
            }
        }

        val jsonObject = JSONObject().apply {
            put("watches", jsonArray)
        }

        context.openFileOutput(localFileName, Context.MODE_PRIVATE).use { stream ->
            stream.write(jsonObject.toString().toByteArray())
        }
    }

    override suspend fun loadWatchesFromLocal(): List<Watch> {
        return try {
            val jsonString = context.openFileInput(localFileName).bufferedReader().use { it.readText() }
            parseWatchesJson(jsonString)
        } catch (e: IOException) {
            // If local file doesn't exist, load from assets
            val jsonString = context.assets.open("sample_watches.json").bufferedReader().use { it.readText() }
            parseWatchesJson(jsonString)
        }
    }

    private suspend fun fetchOnlineData(): List<Watch> = suspendCoroutine { continuation ->
        try {
            val jsonString = URL(onlineJsonUrl).readText()
            val watches = parseWatchesJson(jsonString)
            continuation.resume(watches)
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    private fun parseWatchesJson(jsonString: String): List<Watch> {
        val watches = mutableListOf<Watch>()
        val jsonObject = JSONObject(jsonString)
        val watchesObject = jsonObject.getJSONObject("watches")
        
        watchesObject.keys().forEach { key ->
            val watchJson = watchesObject.getJSONObject(key)
            watches.add(
                Watch(
                    id = watchJson.getString("id"),
                    name = watchJson.getString("name"),
                    brand = watchJson.getString("brand"),
                    price = watchJson.getDouble("price"),
                    description = watchJson.getString("description"),
                    movement = watchJson.optString("movement", "Automatic"),
                    waterResistance = watchJson.optString("waterResistance", "50M"),
                    powerReserve = watchJson.optString("powerReserve", "48 Hours"),
                    caseMaterial = watchJson.optString("caseMaterial", "Stainless Steel")
                )
            )
        }
        return watches
    }
} 