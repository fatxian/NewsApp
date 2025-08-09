package com.androiddevs.mvvmnewsapp.util

import android.app.Application

/**
 * Custom Application class for the app.
 * This class is the entry point of the application and is useful for performing
 * one-time initializations that are needed for the entire app lifecycle.
 *
 * It must be registered in the `AndroidManifest.xml` file in the `<application>` tag
 * using the `android:name` attribute.
 */
class NewsApplication : Application() {
    // Currently empty, but can be used for initializing libraries,
    // dependency injection frameworks (like Hilt or Koin), etc.
}