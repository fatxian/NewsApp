package com.androiddevs.mvvmnewsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androiddevs.mvvmnewsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_create_acc.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.log

/**
 * A Fragment for handling user login.
 * It uses Firebase Authentication for email and password sign-in.
 */
class LoginFragment: Fragment(R.layout.fragment_login) {

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth
    // Current logged-in user, null if no user is logged in
    private var user: FirebaseUser ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Check if a user is already signed in
        user = Firebase.auth.currentUser

        // If user is already logged in, navigate directly to the main fragment
        if(user != null) {
            Log.e("MainActivity", "User already logged in, navigating to MainFragment")
            findNavController().navigate(
                R.id.action_loginFragment_to_mainFragment
            )
        }

        // Set click listener for the login button
        btn_login.setOnClickListener {
            loginUser()
        }

        // Set click listener for the "Create Account" button
        btn_create_acc_login.setOnClickListener{
            findNavController().navigate(
                R.id.action_loginFragment_to_createAccountFragment
            )
        }
    }

    /**
     * Handles the user login process.
     */
    private fun loginUser() {
        // Get email and password from the input fields
        val email = et_email_login.text.toString()
        val password = et_password_login.text.toString()

        // Validate that email and password are not empty
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Use a coroutine to perform the network request on a background thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Attempt to sign in with email and password
                    auth.signInWithEmailAndPassword(email, password).await()
                    // If login is successful, switch to the main thread to update the UI
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(
                            R.id.action_loginFragment_to_mainFragment)
                    }
                } catch (e: Exception) {
                    // If login fails, show an error message on the main thread
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}