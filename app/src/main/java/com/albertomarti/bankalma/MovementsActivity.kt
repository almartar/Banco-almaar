package com.albertomarti.bankalma

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityMovementsBinding
import com.albertomarti.bankalma.ui.AccountsMovementsFragment
import com.example.bancoapiprofe.pojo.Cliente

class MovementsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovementsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val cliente = intent.getSerializableExtra("EXTRA_CLIENTE") as? Cliente
        if (savedInstanceState == null && cliente != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(binding.fragmentContainerMov.id, AccountsMovementsFragment.newInstanceFromCliente(cliente))
                .commit()
        }
    }
}
