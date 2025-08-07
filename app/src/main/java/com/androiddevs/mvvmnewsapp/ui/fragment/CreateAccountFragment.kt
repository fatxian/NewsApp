package com.androiddevs.mvvmnewsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androiddevs.mvvmnewsapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_create_acc.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * A Fragment for handling new user account creation.
 * It uses Firebase Authentication to register users with an email and password.
 */
class CreateAccountFragment : Fragment(R.layout.fragment_create_acc) {

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set click listener for the create account button
        btn_create_acc_create.setOnClickListener{
            registerUser()
        }
    }

    /**
     * Handles the user registration process.
     */
    private fun registerUser() {
        // Get email and password from the input fields
        val email = et_email_create.text.toString()
        val password = et_password_create.text.toString()

        // Validate that email and password are not empty
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Use a coroutine to perform the network request on a background thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Attempt to create a new user with email and password
                    auth.createUserWithEmailAndPassword(email, password).await()
                    // If registration is successful, switch to the main thread to update the UI
                    // and navigate to the main fragment (auto-login)
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(
                            R.id.action_createAccountFragment_to_mainFragment)
                    }
                } catch (e: Exception) {
                    // If registration fails, show an error message on the main thread
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

