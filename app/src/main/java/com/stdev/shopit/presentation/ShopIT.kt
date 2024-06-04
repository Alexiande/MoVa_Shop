package com.stdev.shopit.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShopIT : Application() {
    var darkTheme = false
    override fun onCreate() {
        val savedThemeValue = getSharedPreferences(mova_maker, MODE_PRIVATE).getString(
            THEME_MODE_KEY, "")
        if(savedThemeValue.isNullOrEmpty()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        else{
            when(savedThemeValue){
                "dark" -> {
                    darkTheme = true
                }
                "light" -> {
                    darkTheme = false
                }
            }
            switchTheme(darkTheme)
        }
        super.onCreate()
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {

        const val THEME_MODE_KEY = "key_for_theme_mode"
        const val mova_maker = "playlist_maker_preferences"
    }
}