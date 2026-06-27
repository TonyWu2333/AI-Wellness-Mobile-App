package com.wellnessapp

import android.app.Application
import com.wellnessapp.util.TokenManager

/**
 * Application class for the Wellness App.
 *
 * @author WellnessApp Team
 */
class WellnessApplication : Application() {

    companion object {
        lateinit var instance: WellnessApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        TokenManager.restoreToken()
    }
}
