package com.albertomarti.bankalma.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.albertomarti.bankalma.databinding.ItemListMovementsBinding
import com.example.bancoapiprofe.pojo.Movimiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MovementsAdapter(
    private var movements: List<Movimiento>
) : RecyclerView.Adapter<MovementsAdapter.MovementVH>() {

    inner class MovementVH(val binding: ItemListMovementsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListMovementsBinding.inflate(inflater, parent, false)
        return MovementVH(binding)
    }

    override fun getItemCount(): Int = movements.size

    override fun onBindViewHolder(holder: MovementVH, position: Int) {
        val item = movements[position]
        holder.binding.tvMovementConcept.text = item.getDescripcion()
        holder.binding.tvMovementDate.text = formatDate(item.getFechaOperacion())
        holder.binding.tvMovementAmount.text = item.getImporte().toString()
    }

    fun submitList(newList: List<Movimiento>) {
        movements = newList
        notifyDataSetChanged()
    }

    private fun formatDate(value: Any?): String {
        if (value == null) return ""
        return when (value) {
            is Date -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(value)
            is Long -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(value))
            else -> value.toString()
        }
    }
}


