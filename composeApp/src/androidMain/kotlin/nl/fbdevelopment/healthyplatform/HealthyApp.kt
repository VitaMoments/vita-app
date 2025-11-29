package nl.fbdevelopment.healthyplatform

import android.app.Application
import nl.fbdevelopment.healthyplatform.di.initKoin

class HealthyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}