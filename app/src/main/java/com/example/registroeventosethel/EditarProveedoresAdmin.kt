package com.example.registroeventosethel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditarProveedoresAdmin : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_proveedores)

        val db = SqliteHelper(this)
        val proveedor = intent.getSerializableExtra("proveedor") as Proveedor

        // Usuario y rol actual desde el intent
        val usuarioActual = intent.getStringExtra("usuario") ?: "desconocido"
        val rolUsuario = intent.getStringExtra("rol") ?: "desconocido"

        val etCodigo = findViewById<EditText>(R.id.txnCodigoEP)
        val etNombre = findViewById<EditText>(R.id.txtNombreEP)
        val etDireccion = findViewById<EditText>(R.id.txtDireccionEP)
        val spinnerProvincia = findViewById<Spinner>(R.id.spnProvinciaEP)
        val spinnerDistrito = findViewById<Spinner>(R.id.spnDistritoEP)
        val etCorreo = findViewById<EditText>(R.id.txtCorreoEP)
        val etTelefono = findViewById<EditText>(R.id.txnTelefonoEP)
        val spinnerTipo = findViewById<Spinner>(R.id.spnTipoEP)
        val btnGuardar = findViewById<Button>(R.id.btnSiguienteEP)
        val btnRegresarEP = findViewById<Button>(R.id.btnRegresoPP)

        // Cargar datos del proveedor actual
        etCodigo.setText(proveedor.codigo)
        etNombre.setText(proveedor.nombre)
        etDireccion.setText(proveedor.direccion)
        etCorreo.setText(proveedor.correo)
        etTelefono.setText(proveedor.telefono)

        val adapterProvincias = ArrayAdapter.createFromResource(
            this,
            R.array.provincia_opc,
            android.R.layout.simple_spinner_item
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinnerProvincia.adapter = adapterProvincias

        val adapterDistritos = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapterDistritos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDistrito.adapter = adapterDistritos

// Lógica para actualizar distritos según la provincia seleccionada
        spinnerProvincia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val provincia = parent?.getItemAtPosition(pos).toString()
                val idArrayDistritos = when (provincia) {
                    "Barranca" -> R.array.dist_barranca_opc
                    "Cajatambo" -> R.array.dist_cajatambo_opc
                    "Canta" -> R.array.dist_canta_opc
                    "Cañete" -> R.array.dist_cañete_opc
                    "Huaral" -> R.array.dist_huaral_opc
                    "Huarochirí" -> R.array.dist_huarochiri_opc
                    "Huaura" -> R.array.dist_huaura_opc
                    "Lima provincia" -> R.array.dist_lima_opc
                    "Oyón" -> R.array.dist_oyon_opc
                    "Yauyos" -> R.array.dist_yauyos_opc
                    else -> 0
                }

                adapterDistritos.clear()
                if (idArrayDistritos != 0) {
                    val distritos = resources.getStringArray(idArrayDistritos)
                    adapterDistritos.addAll(*distritos)
                }
                // Seleccionar el distrito original si coincide
                val index = adapterDistritos.getPosition(proveedor.distrito)
                if (index >= 0) {
                    spinnerDistrito.setSelection(index)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

// Preseleccionar provincia
        val indexProvincia = adapterProvincias.getPosition(proveedor.provincia)
        if (indexProvincia >= 0) {
            spinnerProvincia.setSelection(indexProvincia)
        }


        val tipos = listOf("Persona", "Empresa")
        val tipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)
        spinnerTipo.adapter = tipoAdapter
        spinnerTipo.setSelection(tipos.indexOf(proveedor.tipo))

        btnRegresarEP.setOnClickListener {
            startActivity(Intent(this, ListaProveedoresAdmin::class.java))
        }

        btnGuardar.setOnClickListener {
            val proveedorActualizado = Proveedor(
                id = proveedor.id,
                codigo = etCodigo.text.toString(),
                nombre = etNombre.text.toString(),
                direccion = etDireccion.text.toString(),
                provincia = spinnerProvincia.selectedItem.toString(),
                distrito = spinnerDistrito.selectedItem.toString(),
                telefono = etTelefono.text.toString(),
                correo = etCorreo.text.toString(),
                tipo = spinnerTipo.selectedItem.toString(),
                precio = proveedor.precio
            )

            val actualizado = db.actualizarProveedor(proveedorActualizado)
            if (actualizado) {
                // Auditoría
                db.registrarAuditoria(
                    usuario = usuarioActual,
                    rol = rolUsuario,
                    accion = "Modificación",
                    entidad = "Proveedor",
                    detalle = "Proveedor actualizado: ${proveedorActualizado.nombre} (${proveedorActualizado.codigo})"
                )

                Toast.makeText(this, "Proveedor actualizado correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, EditarServicioProvAdmin::class.java)
                intent.putExtra("proveedorId", proveedor.id)
                intent.putExtra("usuario", usuarioActual)
                intent.putExtra("rol", rolUsuario)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error al actualizar proveedor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
