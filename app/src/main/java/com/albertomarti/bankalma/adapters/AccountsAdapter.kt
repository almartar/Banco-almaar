package com.albertomarti.bankalma.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.albertomarti.bankalma.databinding.ItemListAccountsBinding
import com.example.bancoapiprofe.pojo.Cuenta

interface AccountsListener {
    fun onAccountSelected(account: Cuenta)
}

class AccountsAdapter(
    private var accounts: List<Cuenta>,
    private val listener: AccountsListener
) : RecyclerView.Adapter<AccountsAdapter.AccountVH>() {

    inner class AccountVH(val binding: ItemListAccountsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListAccountsBinding.inflate(inflater, parent, false)
        return AccountVH(binding)
    }

    override fun getItemCount(): Int = accounts.size

    override fun onBindViewHolder(holder: AccountVH, position: Int) {
        val item = accounts[position]

        // Construimos el número de cuenta
        val banco = item.getBanco() ?: ""
        val sucursal = item.getSucursal() ?: ""
        val dc = item.getDc() ?: ""
        val num = item.getNumeroCuenta() ?: ""
        val numeroCuenta = banco + "-" + sucursal + "-" + dc + "-" + num
        holder.binding.tvAccountNumber.text = numeroCuenta

        // Formateo simple del saldo y color
        var saldo = item.getSaldoActual()
        if (saldo == null) saldo = 0f
        holder.binding.tvAccountBalance.text = String.format("%.2f", saldo)
        val color = if (saldo >= 0f) Color.parseColor("#2E7D32") else Color.parseColor("#C62828")
        holder.binding.tvAccountBalance.setTextColor(color)

        holder.binding.root.setOnClickListener { listener.onAccountSelected(item) }
    }

    fun submitList(newList: List<Cuenta>) {
        accounts = newList
        notifyDataSetChanged()
    }
}

