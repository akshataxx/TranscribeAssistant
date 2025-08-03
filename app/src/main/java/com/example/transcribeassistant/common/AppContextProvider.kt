package com.example.transcribeassistant.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

object AppContextProvider {
    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
        private set

    fun init(app: Application) {
        context = app.applicationContext
    }
}