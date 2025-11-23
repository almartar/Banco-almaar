package com.albertomarti.bankalma

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityGlobalPositionBinding
import com.albertomarti.bankalma.ui.AccountsFragment
import com.example.bancoapiprofe.pojo.Cliente

class GlobalPositionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGlobalPositionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalPositionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val cliente = intent.getSerializableExtra("EXTRA_CLIENTE") as? Cliente
        if (savedInstanceState == null && cliente != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(binding.fragmentContainer.id, AccountsFragment.newInstance(cliente))
                .commit()
        }
    }
}