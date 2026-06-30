package com.st_louis

import android.app.Application
import com.st_louis.data.ApiClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StLouisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
    }
}
