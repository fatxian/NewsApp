package com.androiddevs.mvvmnewsapp.ui.fragment

import android.os.Bundle
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

class LoginFragment: Fragment(R.layout.fragment_login) {

    lateinit var auth: FirebaseAuth
    var user: FirebaseUser ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        user = Firebase.auth.currentUser

        if(user != null) {
            findNavController().navigate(
                R.id.action_loginFragment_to_mainFragment
            )
        }

        btn_login.setOnClickListener {
            loginUser()
        }

        btn_create_acc_login.setOnClickListener{
            findNavController().navigate(
                R.id.action_loginFragment_to_createAccountFragment
            )
        }
    }

    private fun loginUser() {
        val email = et_email_login.text.toString()
        val password = et_password_login.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(
                            R.id.action_loginFragment_to_mainFragment)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}