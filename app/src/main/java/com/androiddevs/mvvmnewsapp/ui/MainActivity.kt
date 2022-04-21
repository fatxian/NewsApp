package com.androiddevs.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androiddevs.mvvmnewsapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //splash -> activity
        setTheme(R.style.AppTheme)

        setContentView(R.layout.activity_main)

        
    }
}