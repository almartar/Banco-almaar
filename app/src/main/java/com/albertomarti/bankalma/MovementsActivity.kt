package com.albertomarti.bankalma

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.albertomarti.bankalma.databinding.ActivityMovementsBinding
// Importamos las clases de la API de la profe
import com.example.bancoapiprofe.bd.MiBancoOperacional
import com.example.bancoapiprofe.pojo.Cuenta
import com.example.bancoapiprofe.pojo.Movimiento
import com.albertomarti.bankalma.databinding.ItemMovimientoBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Muestra los movimientos de la cuenta seleccionada.
 * Usa Spinner para elegir cuenta y RecyclerView para listar movimientos.
 * Todo síncrono y sin hilos/corutinas/lambdas avanzadas.
 */
class MovementsActivity : AppCompatActivity() {

    // ViewBinding para acceder a las vistas del layout
    private lateinit var binding: ActivityMovementsBinding

    // Lista de cuentas (cuando vengamos desde GlobalPosition podríamos recibir solo una)
    private var cuentas: List<Cuenta> = emptyList()

    // Adaptador del RecyclerView de movimientos
    private lateinit var movimientosAdapter: MovimientosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Llama al ciclo de vida base
        enableEdgeToEdge() // Ajustes de insets para pantalla completa
        binding = ActivityMovementsBinding.inflate(layoutInflater) // Infla layout y binding
        setContentView(binding.root) // Establece el contenido de la Activity

        // Configura el RecyclerView con un layout manager vertical
        binding.rvMovimientos.layoutManager = LinearLayoutManager(this) // Lista vertical

        // Instancia de la API local
        val api = MiBancoOperacional.getInstance(this) // Singleton de fachada

        // Obtenemos la cuenta que nos llega (cuando venimos de GlobalPosition)
        val cuentaInicial = intent.getSerializableExtra("EXTRA_CUENTA") as? Cuenta // Puede ser null

        // Si llega una cuenta, creamos una lista con una sola; si no, podríamos cargar todas del cliente
        cuentas = if (cuentaInicial != null) listOf(cuentaInicial) else emptyList() // Lista para el Spinner

        // Configuramos el Spinner con los números de cuenta como texto
        val textos = cuentas.map { c -> "${c.getBanco()}-${c.getSucursal()}-${c.getDc()}-${c.getNumeroCuenta()}" } // Texto legible
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, textos) // Adaptador simple
        binding.spCuentas.adapter = spinnerAdapter // Asignamos el adaptador al Spinner

        // Cargamos movimientos de la primera cuenta (si existe)
        val movimientos = if (cuentas.isNotEmpty()) {
            @Suppress("UNCHECKED_CAST")
            (api?.getMovimientos(cuentas.first()) as? ArrayList<Movimiento>) ?: arrayListOf() // Lista de movimientos
        } else {
            arrayListOf() // Lista vacía si no hay cuenta
        }

        // Creamos y asignamos el adaptador de movimientos
        movimientosAdapter = MovimientosAdapter(movimientos) // Adaptador del RecyclerView
        binding.rvMovimientos.adapter = movimientosAdapter // Asignamos al RecyclerView

        // Listener del Spinner basado en interface (sin lambdas)
        binding.spCuentas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val cuenta = cuentas[position] // Cuenta seleccionada
                @Suppress("UNCHECKED_CAST")
                val nuevos = (api?.getMovimientos(cuenta) as? ArrayList<Movimiento>) ?: arrayListOf() // Consulta síncrona
                movimientosAdapter.swap(nuevos) // Reemplaza la lista en el adaptador
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No acción necesaria
            }
        }
    }

    /**
     * Adaptador del RecyclerView de movimientos con comentarios línea a línea.
     */
    class MovimientosAdapter(private var data: List<Movimiento>) :
        androidx.recyclerview.widget.RecyclerView.Adapter<MovementsActivity.MovimientosAdapter.MovVH>() {

        // Patrón de la profesora: ViewHolder(view) + ItemMovimientoBinding.bind(view)
        inner class MovVH(view: android.view.View) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
            val binding: ItemMovimientoBinding = ItemMovimientoBinding.bind(view)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): MovVH {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_movimiento, parent, false)
            return MovVH(view)
        }

        override fun onBindViewHolder(holder: MovVH, position: Int) {
            val m = data[position] // Obtenemos el movimiento de esta posición
            val formateador = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formateador de fecha
            val fecha = m.getFechaOperacion()?.let { formateador.format(it) } ?: "" // Convierte Date a texto
            holder.binding.tvDescripcion.text = m.getDescripcion() ?: "" // Asigna descripción
            holder.binding.tvFechaImporte.text = "$fecha  ·  ${holder.itemView.context.getString(R.string.importe_label)}" // Fecha + etiqueta
            val importe = m.getImporte() // Importe como Float
            holder.binding.tvImporte.text = String.format(Locale.getDefault(), "%.2f", importe) // Muestra con 2 decimales
            // Color verde/rojo según signo
            val color = if (importe >= 0f) android.graphics.Color.parseColor("#2E7D32") else android.graphics.Color.parseColor("#C62828")
            holder.binding.tvImporte.setTextColor(color) // Aplica color al texto del importe
        }

        override fun getItemCount(): Int = data.size // Tamaño de la lista actual

        // Reemplaza la lista de datos y refresca la UI
        fun swap(nueva: List<Movimiento>) {
            data = nueva // Sustituye referencia
            notifyDataSetChanged() // Notifica cambios para repintar toda la lista
        }
    }
}
