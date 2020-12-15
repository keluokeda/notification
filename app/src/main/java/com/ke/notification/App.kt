package com.ke.notification

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        _instance = this
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    companion object {
        private var _instance: App? = null

        val instance get() = _instance!!
    }
}

fun String.log() {
    Logger.d(this)
}