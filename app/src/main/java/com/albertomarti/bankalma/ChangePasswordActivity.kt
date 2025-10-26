package com.albertomarti.bankalma

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelar.setOnClickListener { finish() }

        binding.btnAceptar.setOnClickListener {
            val actual = binding.etActual.text?.toString()?.trim().orEmpty()
            val nueva = binding.etNueva.text?.toString()?.trim().orEmpty()
            val confirm = binding.etConfirm.text?.toString()?.trim().orEmpty()

            var ok = true
            if (actual.isEmpty()) { binding.tilActual.error = getString(R.string.error_contrasena_requerida); ok = false } else binding.tilActual.error = null
            if (nueva.isEmpty()) { binding.tilNueva.error = getString(R.string.error_contrasena_requerida); ok = false } else binding.tilNueva.error = null
            if (confirm.isEmpty()) { binding.tilConfirm.error = getString(R.string.error_contrasena_requerida); ok = false } else binding.tilConfirm.error = null
            if (ok && nueva != confirm) { binding.tilConfirm.error = getString(R.string.error_contrasena_no_coincide); ok = false }

            if (!ok) return@setOnClickListener

            Toast.makeText(this, getString(R.string.clave_cambiada_mock), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}


