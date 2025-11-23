package com.albertomarti.bankalma

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.albertomarti.bankalma.databinding.ItemCuentaBinding
import com.example.bancoapiprofe.pojo.Cuenta
import com.albertomarti.bankalma.R

/**
 * Adaptador del RecyclerView de cuentas
 */
class CuentasAdapter(
    private val cuentas: List<Cuenta>, // Lista de cuentas a mostrar
    private val listener: OnCuentaClickListener? = null // clic opcional
) : RecyclerView.Adapter<CuentasAdapter.CuentaVH>() {

    private lateinit var context: Context // Contexto para inflar vistas

    // ViewHolder que vincula el layout del ítem con su binding
    inner class CuentaVH(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ItemCuentaBinding = ItemCuentaBinding.bind(view) // Vinculamos la vista al binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuentaVH {
        // Inflamos el layout XML del ítem (item_cuenta.xml) como en el ejemplo de clase
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_cuenta, parent, false)
        return CuentaVH(view)
    }

    override fun onBindViewHolder(holder: CuentaVH, position: Int) {
        // Asignamos datos al ítem con binding
        val cuenta = cuentas[position]
        val numero = "${cuenta.getBanco()}-${cuenta.getSucursal()}-${cuenta.getDc()}-${cuenta.getNumeroCuenta()}"
        with(holder) {
            binding.tvNumeroCuenta.text = numero
            val saldo = cuenta.getSaldoActual() ?: 0f
            binding.tvSaldo.text = String.format("%.2f", saldo)
            val color = if (saldo >= 0f) android.graphics.Color.parseColor("#2E7D32")
                        else android.graphics.Color.parseColor("#C62828")
            binding.tvSaldo.setTextColor(color)
            binding.root.setOnClickListener { listener?.onCuentaClick(cuenta) } // opcional
        }
    }

    override fun getItemCount(): Int = cuentas.size
}

