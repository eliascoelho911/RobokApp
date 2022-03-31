package com.github.eliascoelho911.robok

import android.app.Application
import com.github.eliascoelho911.robok.di.rubikCubeModule
import com.github.eliascoelho911.robok.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RobokApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RobokApplication)
            modules(viewModelModule, rubikCubeModule)
        }
    }
}