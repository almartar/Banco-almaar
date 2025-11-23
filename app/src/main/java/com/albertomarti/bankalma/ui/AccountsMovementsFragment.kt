package com.albertomarti.bankalma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.albertomarti.bankalma.adapters.MovementsAdapter
import com.albertomarti.bankalma.databinding.FragmentAccountsMovementsBinding
import com.example.bancoapiprofe.bd.MiBancoOperacional
import com.example.bancoapiprofe.pojo.Cuenta
import com.example.bancoapiprofe.pojo.Movimiento
import com.example.bancoapiprofe.pojo.Cliente
import android.widget.ArrayAdapter
import android.widget.AdapterView

class AccountsMovementsFragment : Fragment() {

    private var _binding: FragmentAccountsMovementsBinding? = null
    private val binding get() = _binding!!

    private var account: Cuenta? = null
    private var cliente: Cliente? = null
    private lateinit var adapter: MovementsAdapter
    private var accountsForSpinner: List<Cuenta> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            account = it.getSerializable(ARG_CUENTA) as? Cuenta

            cliente = it.getSerializable(ARG_CLIENTE) as? Cliente
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountsMovementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MovementsAdapter(emptyList())
        binding.recyclerMovements.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMovements.adapter = adapter
        setupSpinnerAndLoad()
    }

    private fun setupSpinnerAndLoad() {
        val api = MiBancoOperacional.getInstance(requireContext())
        // El cliente debe ser el que llega por argumentos
        var lista: ArrayList<Cuenta> = arrayListOf()
        if (cliente != null) {
            val res = api?.getCuentas(cliente)
            if (res != null) {

                lista = res as ArrayList<Cuenta>
            }
        }
        accountsForSpinner = lista

        // Construimos los textos de cada cuenta sin joinToString
        val textos: ArrayList<String> = arrayListOf()
        for (c in accountsForSpinner) {
            val s = (c.getBanco() ?: "") + "-" + (c.getSucursal() ?: "") + "-" +
                    (c.getDc() ?: "") + "-" + (c.getNumeroCuenta() ?: "")
            textos.add(s)
        }
        binding.spAccounts.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, textos)
        // Preseleccionamos la cuenta recibida (si está en la lista)
        val preIndex = accountsForSpinner.indexOfFirst { it.getId() == account?.getId() }
        if (preIndex >= 0) binding.spAccounts.setSelection(preIndex)
        // Listener de cambio: carga movimientos de la cuenta seleccionada
        binding.spAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0 && position < accountsForSpinner.size) {
                    account = accountsForSpinner[position]
                }
                loadMovementsForSelectedAccount()
            }
            override fun onNothingSelected(parent: AdapterView<*>) { /* no-op */ }
        }
        // Carga inicial
        loadMovementsForSelectedAccount()
    }

    private fun loadMovementsForSelectedAccount() {
        val api = MiBancoOperacional.getInstance(requireContext())
        val movimientos = account?.let { a ->
            @Suppress("UNCHECKED_CAST")
            api?.getMovimientos(a) as? List<Movimiento>
        } ?: emptyList()
        adapter.submitList(movimientos)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CUENTA = "ARG_CUENTA"
        private const val ARG_CLIENTE = "ARG_CLIENTE"

        @JvmStatic
        fun newInstance(cuenta: Cuenta): AccountsMovementsFragment =
            AccountsMovementsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CUENTA, cuenta)
                }
            }

        @JvmStatic
        fun newInstanceFromCliente(cliente: Cliente): AccountsMovementsFragment =
            AccountsMovementsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CLIENTE, cliente)
                }
            }
    }
}


