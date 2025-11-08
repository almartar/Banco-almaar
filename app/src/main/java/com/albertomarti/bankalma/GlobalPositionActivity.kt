package com.albertomarti.bankalma

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.albertomarti.bankalma.databinding.ActivityGlobalPositionBinding
// Importamos las clases de la API de la profe usando su paquete original
import com.example.bancoapiprofe.bd.MiBancoOperacional
import com.example.bancoapiprofe.pojo.Cliente
import com.example.bancoapiprofe.pojo.Cuenta

/**
 * Muestra la posición global del cliente: sus cuentas en un RecyclerView.
 *
 */
class GlobalPositionActivity : AppCompatActivity(), OnCuentaClickListener {

    // ViewBinding para acceder a las vistas del layout
    private lateinit var binding: ActivityGlobalPositionBinding

    // Adaptador del RecyclerView de cuentas
    private lateinit var cuentasAdapter: CuentasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Llama al ciclo de vida base de la activity
        enableEdgeToEdge() // Ajusta la UI a pantalla completa respetando los insets del sistema
        binding = ActivityGlobalPositionBinding.inflate(layoutInflater) // Infla el layout y crea el binding
        setContentView(binding.root) // Establece la vista raíz como contenido

        // Configuramos el RecyclerView con un LinearLayoutManager vertical
        binding.rvCuentas.layoutManager = LinearLayoutManager(this) // Lista vertical para las cuentas

        // Recuperamos el Cliente serializable que viene desde Login/Main
        val cliente = intent.getSerializableExtra("EXTRA_CLIENTE") as? Cliente // Puede ser null si no se pasó

        // Obtenemos una instancia de la API de Ana
        val api = MiBancoOperacional.getInstance(this) // Singleton de acceso a BD/DAOs

        // Si tenemos cliente, pedimos sus cuentas; si no, lista vacía
        val cuentas = if (cliente != null) {

            (api?.getCuentas(cliente) as? ArrayList<Cuenta>) ?: arrayListOf() // Conversión segura a ArrayList<Cuenta>
        } else {
            arrayListOf() // Sin cliente no hay cuentas
        }

        // Creamos el adaptador pasando la lista y este Activity como listener (sin lambdas)
        cuentasAdapter = CuentasAdapter(cuentas, this)

        // Asignamos el adaptador al RecyclerView
        binding.rvCuentas.adapter = cuentasAdapter // Pinta la lista de cuentas
    }

    // Implementación del  clic en cuenta
    override fun onCuentaClick(cuenta: Cuenta) {
        // Al pulsar una cuenta navegamos a MovementsActivity con la cuenta seleccionada
        val i = Intent(this, MovementsActivity::class.java) // Intent explícito a la pantalla de movimientos
        i.putExtra("EXTRA_CUENTA", cuenta) // Pasamos la cuenta como Serializable
        startActivity(i) // Abrimos la pantalla de movimientos
    }
}