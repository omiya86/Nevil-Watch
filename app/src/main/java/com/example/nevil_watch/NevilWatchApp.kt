package com.example.nevil_watch

import android.app.Application
import com.example.nevil_watch.data.WatchRepository
import com.example.nevil_watch.data.WatchRepositoryImpl

class NevilWatchApp : Application() {
    lateinit var repository: WatchRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = WatchRepositoryImpl(this)
    }
} 