package com.albertomarti.bankalma

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityMainBinding
import com.example.bancoapiprofe.pojo.Cliente

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cliente = intent.getSerializableExtra("EXTRA_CLIENTE") as? Cliente
        val dni = cliente?.getNif().orEmpty()
        binding.tvWelcomeTitle.text = getString(R.string.bienvenido)
        binding.tvWelcomeDni.text = dni

        binding.btnCambiarClave.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        binding.btnSalirMain.setOnClickListener {
            val i = Intent(this, WelcomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }

        binding.btnPosicionGlobal.setOnClickListener {
            val i = Intent(this, GlobalPositionActivity::class.java)
            if (cliente != null) i.putExtra("EXTRA_CLIENTE", cliente)
            startActivity(i)
        }
        binding.btnMovimientos.setOnClickListener {
            val i = Intent(this, MovementsActivity::class.java)
            if (cliente != null) i.putExtra("EXTRA_CLIENTE", cliente)
            startActivity(i)
        }
        binding.btnTransferencias.setOnClickListener {
            try {
                val it = Intent(this, TransferActivity::class.java)
                if (cliente != null) it.putExtra("EXTRA_CLIENTE", cliente)
                startActivity(it)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al abrir: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        val msg = getString(R.string.funcionalidad_pendiente)
        binding.btnPromociones.setOnClickListener { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
        binding.btnCajeros.setOnClickListener { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }
}