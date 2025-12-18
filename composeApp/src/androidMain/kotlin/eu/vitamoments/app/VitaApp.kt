package eu.vitamoments.app

import android.app.Application
import eu.vitamoments.app.di.initKoin

class VitaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}