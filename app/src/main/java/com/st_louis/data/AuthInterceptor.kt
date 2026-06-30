package com.st_louis.data

import android.content.Context
import com.st_louis.utils.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val preferenceManager = PreferenceManager.getInstance(context)
        val token = preferenceManager.getToken()
        
        val request = chain.request().newBuilder()
        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }
        
        return chain.proceed(request.build())
    }
}