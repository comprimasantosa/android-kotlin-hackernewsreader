package com.primasantosa.android.hackernewsreader

import android.app.Application
import timber.log.Timber

class HackerNewsReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}