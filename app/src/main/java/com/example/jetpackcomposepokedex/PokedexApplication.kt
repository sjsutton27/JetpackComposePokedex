package com.example.jetpackcomposepokedex

import android.app.Application
import com.example.jetpackcomposepokedex.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class PokedexApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //startKoin is used to start the Koin dependency injection framework.
        startKoin {
            androidContext(this@PokedexApplication)
            modules(appModule)
        }
        // Timber is a logging library used for debugging and logging in Android applications. This is just initializing Timber
        Timber.plant(Timber.DebugTree())
    }
}
