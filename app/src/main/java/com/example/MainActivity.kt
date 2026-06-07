package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create viewModel instance
        val viewModel = ViewModelProvider(this)[HisaabViewModel::class.java]

        setContent {
            HisaabApp(viewModel = viewModel)
        }
    }
}
