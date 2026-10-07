package com.rentaya.app

import android.app.Application
import org.osmdroid.config.Configuration
import java.io.File

class RentaYaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().apply {
            userAgentValue = "RentaYa/${BuildConfig.VERSION_NAME} (${packageName})"
            osmdroidBasePath = cacheDir
            osmdroidTileCache = File(cacheDir, "osmdroid")
            load(this@RentaYaApplication, getSharedPreferences("osmdroid", MODE_PRIVATE))
        }
    }
}
