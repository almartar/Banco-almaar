package com.albertomarti.bankalma

import com.example.bancoapiprofe.pojo.Cuenta

/**
 * Interface simple para manejar clics en cuentas
 */
interface OnCuentaClickListener {
    fun onCuentaClick(cuenta: Cuenta)
}


