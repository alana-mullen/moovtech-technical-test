package co.uk.thewirelessguy.moovtech

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.wosmart.ukprotocollibary.WristbandManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        initLogging()

        WristbandManager.getInstance(this)
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}