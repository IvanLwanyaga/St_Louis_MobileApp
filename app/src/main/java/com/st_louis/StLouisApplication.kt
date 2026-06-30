package com.st_louis

import android.app.Application
import com.st_louis.data.ApiClient

class StLouisApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
    }
}