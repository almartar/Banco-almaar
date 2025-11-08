package com.albertomarti.bankalma

import android.os.Bundle
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityLoginBinding
// Importamos las clases de la API de la profe
import com.example.bancoapiprofe.bd.MiBancoOperacional
import com.example.bancoapiprofe.pojo.Cliente

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

            // Login real contra la API local (síncrono, sin hilos/corutinas)
            val api = MiBancoOperacional.getInstance(this) // Obtiene la fachada de acceso a BD
            val c = Cliente().apply { // Construye un Cliente parcial con nif y clave
                setNif(dni)
                setClaveSeguridad(password)
            }
            val logged = api?.login(c) // Intenta autenticar; devuelve Cliente completo o null
            if (logged == null) {
                // Si las credenciales no son válidas, mostramos un error simple en usuario
                binding.tilUser.error = getString(R.string.error_dni_requerido)
                return@setOnClickListener
            }

            // Si OK, pasamos el Cliente completo a la siguiente pantalla
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("EXTRA_CLIENTE", logged) // Cliente es Serializable
            }
            startActivity(intent) // Navega a la pantalla principal
            finish() // Cierra el login para no volver con “Atrás”
        }

        binding.btnSalir.setOnClickListener {
            finishAffinity()
        }

    }
}