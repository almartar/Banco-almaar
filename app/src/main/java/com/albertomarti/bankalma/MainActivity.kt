package com.albertomarti.bankalma

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

        // Acciones de los botones (mock salvo cambiar clave y salir)
        binding.btnCambiarClave.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        binding.btnSalirMain.setOnClickListener {
            val i = Intent(this, WelcomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }

        val toastMsg = getString(R.string.funcionalidad_pendiente)
        binding.btnPosicionGlobal.setOnClickListener { Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show() }
        binding.btnMovimientos.setOnClickListener { Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show() }
        binding.btnTransferencias.setOnClickListener { Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show() }
        binding.btnPromociones.setOnClickListener { Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show() }
        binding.btnCajeros.setOnClickListener { Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show() }
    }
}