package com.albertomarti.bankalma

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityGlobalPositionDetailsBinding
import com.albertomarti.bankalma.ui.AccountsMovementsFragment
import com.example.bancoapiprofe.pojo.Cuenta

class GlobalPositionDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGlobalPositionDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalPositionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val cuenta = intent.getSerializableExtra("EXTRA_CUENTA") as? Cuenta
        if (savedInstanceState == null && cuenta != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    binding.fragmentContainerDetails.id,
                    AccountsMovementsFragment.newInstance(cuenta)
                )
                .commit()
        }
    }
}


