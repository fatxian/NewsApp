package com.androiddevs.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.androiddevs.mvvmnewsapp.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * The main and only activity in this application.
 * It follows a single-activity architecture and hosts all the fragments.
 * Its primary responsibilities are setting up the window for edge-to-edge display
 * and holding the NavHostFragment which manages app navigation.
 */
class MainActivity : AppCompatActivity() {

    // Variable to store the height of the status bar.
    private var statusBarHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Switch from the splash screen theme to the main app theme.
        setTheme(R.style.AppTheme)

        // Set the content view to the main activity layout.
        setContentView(R.layout.activity_main)

        // Configure the window for an edge-to-edge UI.
        setupWindow()
    }

    /**
     * Configures the window to draw behind the system bars (status and navigation bars).
     * This creates an immersive, edge-to-edge user interface.
     */
    private fun setupWindow() {
        // Tell the window that the app will handle drawing behind the system bars.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Add a listener to get the insets of the system windows (like status bar).
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            // Get the insets for system bars (status bar, navigation bar) and IME (keyboard).
            val systemWindowInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            // Store the top inset, which is the height of the status bar.
            statusBarHeight = systemWindowInsets.top
            // Return the insets to allow child views to consume them.
            insets
        }
    }

    /**
     * Provides public access to the status bar height.
     * Fragments can call this method to properly pad their UI elements.
     * @return The height of the status bar in pixels.
     */
    fun getStatusBarHeight(): Int {
        return statusBarHeight
    }
}