package com.albertomarti.bankalma

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityLoginBinding
// Importamos las clases de la API de la profe
import com.example.bancoapiprofe.bd.MiBancoOperacional
import com.example.bancoapiprofe.pojo.Cliente

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEntrar.setOnClickListener {
            val dni = binding.etUser.text?.toString()?.trim().orEmpty() // Lee y recorta el DNI (o "")
            val password = binding.etContrasena.text?.toString()?.trim().orEmpty() // Lee y recorta la clave (o "")

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

            val api = MiBancoOperacional.getInstance(this)
            val c = Cliente().apply {
                setNif(dni)
                setClaveSeguridad(password)
            }
            val logged = api?.login(c)
            if (logged == null) {
                binding.tilUser.error = getString(R.string.error_dni_requerido)
                return@setOnClickListener
            }

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("EXTRA_CLIENTE", logged)
            }
            startActivity(intent)
            finish()
        }

        binding.btnSalir.setOnClickListener {
            finishAffinity()
        }

    }
}