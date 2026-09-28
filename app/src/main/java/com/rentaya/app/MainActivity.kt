package com.rentaya.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.rentaya.app.data.UserPreferences
import com.rentaya.app.ui.navigation.RentaYaApp
import com.rentaya.app.ui.theme.RentaYaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val userPreferences = UserPreferences(applicationContext)
        
        setContent {
            val darkMode by userPreferences.darkMode.collectAsState(initial = false)
            val isLoggedIn by userPreferences.isLoggedIn.collectAsState(initial = false)
            
            RentaYaTheme(darkTheme = darkMode) {
                RentaYaApp(
                    userPreferences = userPreferences,
                    isLoggedIn = isLoggedIn
                )
            }
        }
    }
}
