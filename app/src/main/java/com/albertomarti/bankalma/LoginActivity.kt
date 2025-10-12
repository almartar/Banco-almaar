package com.albertomarti.bankalma

import android.os.Bundle
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEntrar.setOnClickListener {
            val dni = binding.etUser.text?.toString()?.trim().orEmpty()
            val password = binding.etContrasena.text?.toString()?.trim().orEmpty()

            var isValid = true

            if (dni.isEmpty()) {
                binding.tilUser.error = getString(R.string.error_dni_requerido)
                isValid = false
            } else {
                binding.tilUser.error = null
            }

            if (password.isEmpty()) {
                binding.tilContrasena.error = getString(R.string.error_contrasena_requerida)
                isValid = false
            } else {
                binding.tilContrasena.error = null
            }

            if (!isValid) return@setOnClickListener

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("EXTRA_DNI", dni)
            }
            startActivity(intent)
            finish()
        }

        binding.btnSalir.setOnClickListener {
            finishAffinity()
        }

    }
}