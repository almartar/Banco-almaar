package com.albertomarti.bankalma.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.albertomarti.bankalma.GlobalPositionDetailsActivity
import com.albertomarti.bankalma.adapters.AccountsAdapter
import com.albertomarti.bankalma.adapters.AccountsListener
import com.albertomarti.bankalma.databinding.FragmentAccountsBinding
import com.example.bancoapiprofe.bd.MiBancoOperacional
import com.example.bancoapiprofe.pojo.Cliente
import com.example.bancoapiprofe.pojo.Cuenta

class AccountsFragment : Fragment(), AccountsListener {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!

    private var cliente: Cliente? = null
    private lateinit var adapter: AccountsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            cliente = it.getSerializable(ARG_CLIENTE) as? Cliente
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AccountsAdapter(emptyList(), this)
        binding.recyclerAccounts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAccounts.adapter = adapter

        loadAccounts()
    }

    private fun loadAccounts() {
        val api = MiBancoOperacional.getInstance(requireContext())
        val cuentas = cliente?.let { c ->
            @Suppress("UNCHECKED_CAST")
            api?.getCuentas(c) as? List<Cuenta>
        } ?: emptyList()
        adapter.submitList(cuentas)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAccountSelected(account: Cuenta) {
        val ctx = requireContext()
        val intent = Intent(ctx, GlobalPositionDetailsActivity::class.java).apply {
            putExtra("EXTRA_CUENTA", account)
        }
        startActivity(intent)
    }

    companion object {
        private const val ARG_CLIENTE = "ARG_CLIENTE"

        @JvmStatic
        fun newInstance(c: Cliente): AccountsFragment =
            AccountsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CLIENTE, c)
                }
            }
    }
}


