package com.androiddevs.mvvmnewsapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A container Fragment that holds the main navigation for the app,
 * including the BottomNavigationView and a NavHostFragment for displaying
 * the primary content fragments (BreakingNews, SavedNews, SearchNews).
 */
class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Navigation Setup ---

        // Find the NavHostFragment within this fragment's layout.
        // We use childFragmentManager because this is a nested NavHostFragment.
        val navHostFragment = childFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment

        // Get the NavController from the NavHostFragment.
        val navController = navHostFragment.navController

        // Connect the BottomNavigationView with the NavController.
        // This automatically handles navigation when a bottom navigation item is tapped.
        bottomNavigationView.setupWithNavController(navController)
    }
}
