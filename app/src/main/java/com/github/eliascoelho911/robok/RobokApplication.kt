package com.github.eliascoelho911.robok

import android.app.Application
import com.github.eliascoelho911.robok.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RobokApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModules)
            androidContext(this@RobokApplication)
        }
    }
}