package com.albertomarti.bankalma

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dni = intent.getStringExtra("EXTRA_DNI").orEmpty()
        binding.tvWelcomeTitle.text = getString(R.string.bienvenido)
        binding.tvWelcomeDni.text = dni
    }
}