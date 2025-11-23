# Banco-almaar (Tema 6) - Documentación para examen

Este documento resume la aplicación Android realizada en Kotlin con vistas XML siguiendo el temario del Tema 6. Incluye el flujo funcional, explicación de cada pantalla/clase y fragment, y fragmentos de código clave. El proyecto evita características avanzadas no vistas en clase (sin corutinas, sin extensiones KTX “modernas”) y usa paso de datos mediante `Bundle` + `Serializable` como en los apuntes.

---

## 1. Flujo general de la app

1) WelcomeActivity → botón “Empezar” abre LoginActivity.  
2) LoginActivity → valida usuario con la API local (`MiBancoOperacional.login`) y abre MainActivity pasando un `Cliente` (Serializable) como `EXTRA_CLIENTE`.  
3) MainActivity → menú con botones.  
   - “Posición global” → abre GlobalPositionActivity, donde se aloja el fragment `AccountsFragment` que muestra todas las cuentas del cliente.  
   - “Movimientos” → abre MovementsActivity, que aloja `AccountsMovementsFragment`: muestra un Spinner con las cuentas del cliente y, al elegir una, lista sus movimientos.  
   - “Transferencias” → `TransferActivity`: simula envío mostrando un Toast con el resumen.  
   - “Cambiar contraseña” → `ChangePasswordActivity` (mock simple).  

Datos y API:
- API local en `com.example.bancoapiprofe.bd.MiBancoOperacional` con BD SQLite (`MiBD`) y DAOs.  
- POJOs `Cliente`, `Cuenta`, `Movimiento` (Serializable).  

---

## 2. Pantallas (Activities) y su propósito

### 2.1 WelcomeActivity
Pantalla inicial muy simple que navega al login.

```kotlin
class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
```

### 2.2 LoginActivity
Lee DNI y contraseña, valida campos, llama a `MiBancoOperacional.login` y, si es correcto, pasa el `Cliente` a `MainActivity`.

```kotlin
val api = MiBancoOperacional.getInstance(this)
val c = Cliente().apply {
    setNif(dni)
    setClaveSeguridad(password)
}
val logged = api?.login(c)
if (logged != null) {
    val i = Intent(this, MainActivity::class.java)
    i.putExtra("EXTRA_CLIENTE", logged)  // Serializable
    startActivity(i)
    finish()
}
```

### 2.3 MainActivity
Recibe el `Cliente` y ofrece botones para abrir el resto de pantallas, reenviando `EXTRA_CLIENTE` cuando procede.

```kotlin
binding.btnPosicionGlobal.setOnClickListener {
    val i = Intent(this, GlobalPositionActivity::class.java)
    i.putExtra("EXTRA_CLIENTE", cliente)
    startActivity(i)
}

binding.btnMovimientos.setOnClickListener {
    val i = Intent(this, MovementsActivity::class.java)
    i.putExtra("EXTRA_CLIENTE", cliente)
    startActivity(i)
}
```

### 2.4 GlobalPositionActivity (host de fragment)
Aloja el fragment de cuentas. El layout `activity_global_position.xml` contiene solo un `FragmentContainerView`.

```kotlin
class GlobalPositionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGlobalPositionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalPositionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val cliente = intent.getSerializableExtra("EXTRA_CLIENTE") as? Cliente
        if (savedInstanceState == null && cliente != null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, AccountsFragment.newInstance(cliente))
                .commit()
        }
    }
}
```

### 2.5 MovementsActivity (host de fragment)
Igual que la anterior, pero para el fragment con `Spinner` de cuentas y lista de movimientos.

```kotlin
class MovementsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovementsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovementsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val cliente = intent.getSerializableExtra("EXTRA_CLIENTE") as? Cliente
        if (savedInstanceState == null && cliente != null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerMov.id,
                         AccountsMovementsFragment.newInstanceFromCliente(cliente))
                .commit()
        }
    }
}
```

### 2.6 TransferActivity
Formulario con Spinners y RadioButtons. El botón “Enviar” muestra un Toast con el resumen.

```kotlin
binding.btnEnviar.setOnClickListener {
    val origen = binding.spOrigen.selectedItem?.toString().orEmpty()
    val destino = if (binding.rbPropia.isChecked)
        binding.spDestino.selectedItem?.toString().orEmpty()
    else
        binding.etDestino.text?.toString()?.trim().orEmpty()
    val importe = binding.etImporte.text?.toString()?.trim().orEmpty()
    val divisa = binding.spDivisa.selectedItem?.toString().orEmpty()
    val just = if (binding.cbJustificante.isChecked) getString(R.string.si) else getString(R.string.no)
    val etiqueta = if (binding.rbPropia.isChecked) "A cuenta propia" else "A cuenta ajena"
    val resumen = "Cuenta origen:\n$origen\n$etiqueta:\n$destino\nImporte: $importe $divisa\nEnviar justificante: $just"
    Toast.makeText(this, resumen, Toast.LENGTH_LONG).show()
}
```

---

## 3. Fragments

### 3.1 AccountsFragment (lista de cuentas del cliente)
Se crea con `newInstance(cliente)` y recupera el objeto en `onCreate`. Pinta un `RecyclerView` con las cuentas.

```kotlin
companion object {
    private const val ARG_CLIENTE = "ARG_CLIENTE"
    @JvmStatic
    fun newInstance(c: Cliente): AccountsFragment =
        AccountsFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_CLIENTE, c) }
        }
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    cliente = arguments?.getSerializable(ARG_CLIENTE) as? Cliente
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    adapter = AccountsAdapter(emptyList(), object : AccountsListener {
        override fun onAccountSelected(account: Cuenta) { /* sin navegación */ }
    })
    binding.recyclerAccounts.layoutManager = LinearLayoutManager(requireContext())
    binding.recyclerAccounts.adapter = adapter
    // Carga de cuentas
    val api = MiBancoOperacional.getInstance(requireContext())
    val cuentas = (api?.getCuentas(cliente) as? List<Cuenta>) ?: emptyList()
    adapter.submitList(cuentas)
}
```

### 3.2 AccountsMovementsFragment (Spinner + movimientos)
Se prefiere el `Cliente` pasado por argumentos (según el temario). Con ese cliente se rellenan las cuentas del Spinner y, al seleccionar, se listan los movimientos.

```kotlin
companion object {
    private const val ARG_CLIENTE = "ARG_CLIENTE"
    @JvmStatic
    fun newInstanceFromCliente(cliente: Cliente) =
        AccountsMovementsFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_CLIENTE, cliente) }
        }
}

private fun setupSpinnerAndLoad() {
    val api = MiBancoOperacional.getInstance(requireContext())
    var lista: ArrayList<Cuenta> = arrayListOf()
    if (cliente != null) {
        val res = api?.getCuentas(cliente)
        if (res != null) lista = res as ArrayList<Cuenta>
    }
    accountsForSpinner = lista

    val textos = ArrayList<String>()
    for (c in accountsForSpinner) {
        val s = (c.getBanco() ?: "") + "-" + (c.getSucursal() ?: "") + "-" +
                (c.getDc() ?: "") + "-" + (c.getNumeroCuenta() ?: "")
        textos.add(s)
    }
    binding.spAccounts.adapter =
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, textos)

    binding.spAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
            if (pos >= 0 && pos < accountsForSpinner.size) account = accountsForSpinner[pos]
            loadMovementsForSelectedAccount()
        }
        override fun onNothingSelected(p: AdapterView<*>) { }
    }
    loadMovementsForSelectedAccount()
}

private fun loadMovementsForSelectedAccount() {
    val api = MiBancoOperacional.getInstance(requireContext())
    val movimientos = if (account != null)
        (api?.getMovimientos(account) as? List<Movimiento>) ?: emptyList()
    else emptyList()
    adapter.submitList(movimientos)
}
```

---

## 4. Adapters (RecyclerView)

### 4.1 AccountsAdapter (fragment de cuentas)
Código sencillo (Kotlin básico) para pintar número de cuenta y saldo con color.

```kotlin
class AccountsAdapter(
    private var accounts: List<Cuenta>,
    private val listener: AccountsListener
) : RecyclerView.Adapter<AccountsAdapter.AccountVH>() {

    inner class AccountVH(val binding: ItemListAccountsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: AccountVH, position: Int) {
        val item = accounts[position]
        val banco = item.getBanco() ?: ""
        val sucursal = item.getSucursal() ?: ""
        val dc = item.getDc() ?: ""
        val num = item.getNumeroCuenta() ?: ""
        holder.binding.tvAccountNumber.text = "$banco-$sucursal-$dc-$num"
        var saldo = item.getSaldoActual(); if (saldo == null) saldo = 0f
        holder.binding.tvAccountBalance.text = String.format("%.2f", saldo)
        val color = if (saldo >= 0f) Color.parseColor("#2E7D32") else Color.parseColor("#C62828")
        holder.binding.tvAccountBalance.setTextColor(color)
    }

    fun submitList(newList: List<Cuenta>) {
        accounts = newList
        notifyDataSetChanged()
    }
}
```

### 4.2 MovementsAdapter (fragment de movimientos)
Formatea la fecha y el importe.

```kotlin
override fun onBindViewHolder(holder: MovementVH, position: Int) {
    val item = movements[position]
    holder.binding.tvMovementConcept.text = item.getDescripcion()
    holder.binding.tvMovementDate.text = formatDate(item.getFechaOperacion())
    holder.binding.tvMovementAmount.text = item.getImporte().toString()
}

private fun formatDate(value: Any?): String {
    if (value == null) return ""
    return if (value is Date) SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(value)
    else value.toString()
}
```

---

## 5. API local y modelo de datos

- `MiBancoOperacional`: fachada estática con `getInstance(context)`. Operaciones usadas: `login(cliente)`, `getCuentas(cliente)`, `getMovimientos(cuenta)`.
- `MiBD`: gestión SQLite; crea tablas de `clientes`, `cuentas`, `movimientos` y rellena con datos de ejemplo.
- DAOs (`ClienteDAO`, `CuentaDAO`, `MovimientoDAO`) realizan las consultas a SQLite.
- POJOs (`Cliente`, `Cuenta`, `Movimiento`) son `Serializable` para pasar por `Intent`/`Bundle` como en el temario.

---

## 6. Layouts principales

- `activity_global_position.xml`: `FragmentContainerView`.
- `fragment_accounts.xml`: título “Posición global”, subtítulo “Cuentas:” y `RecyclerView`.
- `item_list_accounts.xml`: `MaterialCardView` con icono, número de cuenta y saldo.
- `fragment_accounts_movements.xml`: “Selecciona la cuenta:”, `Spinner` y `RecyclerView`.
- `activity_movements.xml`: contenedor para el fragment de movimientos.

---

## 7. Cómo compilar y probar

1) Ejecutar la app.  
2) Welcome → Login (DNI y contraseña de ejemplo están cargados en la BD de `MiBD`).  
3) Main → Posición global: ver cuentas con saldo (verde/rojo).  
4) Main → Movimientos: elegir cuenta en el Spinner y ver lista de movimientos.  
5) Main → Transferencias: completar y pulsar “Enviar” para ver el Toast con el resumen.  

---

## 8. Exportar a PDF

- Opción rápida: abre este archivo en el editor de Markdown y usa “Print/Export to PDF” (en VSCode, Android Studio o tu visor Markdown preferido).  
- O con `pandoc` (si lo tienes instalado):

```bash
pandoc docs/Documentacion_Proyecto_Tema6.md -o docs/Documentacion_Proyecto_Tema6.pdf
```

---

## 9. Notas de estilo (alineadas con el temario)

- Paso de objetos entre Activity/Fragment con `Serializable` y `Bundle` (`newInstance` + `onCreate`).  
- Kotlin básico: `if/else`, concatenación con `+`, sin operadores avanzados.  
- Layouts XML sencillos con componentes estándar y `MaterialCardView` para mejorar legibilidad en móvil.  


