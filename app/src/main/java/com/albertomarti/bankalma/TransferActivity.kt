package com.albertomarti.bankalma

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.albertomarti.bankalma.databinding.ActivityTransferBinding
// API de la profesora (usamos sus paquetes originales)
import com.example.bancoapiprofe.bd.MiBancoOperacional
import com.example.bancoapiprofe.pojo.Cliente
import com.example.bancoapiprofe.pojo.Cuenta

/**
 * TransferActivity (solo lo pedido por la actividad):
 * - Spinners de cuentas (cargadas desde la API del cliente logueado)
 * - RadioButtons para alternar destino propia/ajena (visibilidad Spinner/EditText)
 * - Importe + divisa (Spinner)
 * - Checkbox justificante
 * - Botones Enviar/Cancelar: Enviar muestra un Toast resumen; Cancelar limpia todo
 * - Sin lambdas: listeners con interfaces explícitas
 */
class TransferActivity : AppCompatActivity() {

    // ViewBinding para acceder a las vistas del layout
    private lateinit var binding: ActivityTransferBinding

    // Lista de cuentas reales recuperadas de la API (se mantiene para usar por índice)
    private var cuentasApi: ArrayList<Cuenta> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Inicializa ciclo de vida
        enableEdgeToEdge() // Ajustes de insets
        binding = ActivityTransferBinding.inflate(layoutInflater) // Infla el layout y crea el binding
        setContentView(binding.root) // Establece el contenido

        // 1) Recuperamos el cliente que viene desde Main/Login (puede ser null)
        val cliente = intent.getSerializableExtra("EXTRA_CLIENTE") as? Cliente

        // 2) Obtenemos la fachada de la API de la profe
        val api = MiBancoOperacional.getInstance(this)

        // 3) Cargamos las cuentas del cliente si existe; si no, lista vacía (sin lambdas)
        cuentasApi = ArrayList()
        if (cliente != null) {
            @Suppress("UNCHECKED_CAST")
            cuentasApi = (api?.getCuentas(cliente) as? ArrayList<Cuenta>) ?: arrayListOf()
        }

        // 4) Construimos la lista de textos para los Spinners (sin lambdas: con bucle)
        val opcionesCuentas = ArrayList<String>()
        for (c in cuentasApi) {
            val texto = c.getBanco() + "-" + c.getSucursal() + "-" + c.getDc() + "-" + c.getNumeroCuenta()
            opcionesCuentas.add(texto)
        }

        // 5) Divisas de ejemplo (en código, no arrays.xml)
        val opcionesDivisa = arrayListOf("EUR", "USD", "GBP")

        // 6) Adaptadores de los Spinners
        val adapterCuentas = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesCuentas)
        val adapterDivisa = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesDivisa)
        binding.spOrigen.adapter = adapterCuentas
        binding.spDestino.adapter = adapterCuentas
        binding.spDivisa.adapter = adapterDivisa

        // 7) Listeners de los RadioButtons para alternar visibilidad sin lambdas
        binding.rbPropia.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    binding.spDestino.visibility = View.VISIBLE
                    binding.tilDestino.visibility = View.GONE
                }
            }
        })
        binding.rbAjena.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    binding.spDestino.visibility = View.GONE
                    binding.tilDestino.visibility = View.VISIBLE
                }
            }
        })

        // 8) Botón Enviar: muestra Toast con resumen (sin tocar BD)
        binding.btnEnviar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Origen desde Spinner (texto mostrado)
                val origen = binding.spOrigen.selectedItem?.toString().orEmpty()
                // Destino: si propia, otro Spinner; si ajena, EditText
                val destino: String = if (binding.rbPropia.isChecked) {
                    binding.spDestino.selectedItem?.toString().orEmpty()
                } else {
                    binding.etDestino.text?.toString()?.trim().orEmpty()
                }
                // Importe y divisa
                val importe = binding.etImporte.text?.toString()?.trim().orEmpty()
                val divisa = binding.spDivisa.selectedItem?.toString().orEmpty()
                // Justificante como texto Sí/No
                val justificante = if (binding.cbJustificante.isChecked) getString(R.string.si) else getString(R.string.no)

                // Validaciones mínimas
                if (origen.isEmpty()) {
                    Toast.makeText(this@TransferActivity, getString(R.string.error_origen_requerido), Toast.LENGTH_SHORT).show()
                    return
                }
                if (!binding.rbPropia.isChecked && destino.isEmpty()) {
                    binding.tilDestino.error = getString(R.string.error_destino_requerido)
                    return
                } else {
                    binding.tilDestino.error = null
                }
                if (importe.isEmpty()) {
                    Toast.makeText(this@TransferActivity, getString(R.string.error_importe_requerido), Toast.LENGTH_SHORT).show()
                    return
                }

                // Resumen por Toast (simulado, sin persistencia)
                val resumen = (
                    getString(R.string.cuenta_origen) + ":\n" + origen + "\n" +
                    getString(R.string.cuenta_destino) + ":\n" + destino + "\n" +
                    getString(R.string.importe_label) + " " + importe + " " + divisa + "\n" +
                    getString(R.string.enviar_justificante) + ": " + justificante
                )
                Toast.makeText(this@TransferActivity, resumen, Toast.LENGTH_LONG).show()
            }
        })

        // 9) Botón Cancelar: vuelve atrás (cerrar Activity)
        binding.btnCancelar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // Cierra TransferActivity y vuelve a la anterior
            }
        })
    }
}


