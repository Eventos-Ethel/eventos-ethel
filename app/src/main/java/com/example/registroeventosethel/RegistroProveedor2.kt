package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegistroProveedor2 : AppCompatActivity() {

    private lateinit var dbHelper: SqliteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_proveedor2)

        dbHelper = SqliteHelper(this)

        val usuarioActual = intent.getStringExtra("usuario") ?: "desconocido"
        val rolUsuario = intent.getStringExtra("rol") ?: "desconocido"

        val etCodigo = findViewById<EditText>(R.id.txnCodigoRP2)
        val spnTipoProvRP2 = findViewById<Spinner>(R.id.spnTipoProvRP2)
        val etNombre = findViewById<EditText>(R.id.txtNombreRP2)
        val etDireccion = findViewById<EditText>(R.id.txtDireccionRP2)
        val spnProvinciaRP2 = findViewById<Spinner>(R.id.spnProvinciaRP2)
        val spnDistritoRP2 = findViewById<Spinner>(R.id.spnDistritoRP2)
        val etTelefono = findViewById<EditText>(R.id.txnTelefonoRP2)
        val etCorreo = findViewById<EditText>(R.id.txtCorreoRP2)
        val btnSiguiente = findViewById<Button>(R.id.btnSiguienteRP2)
        val btnRegresar = findViewById<Button>(R.id.btnRegresarRP2)

        etCodigo.setText(dbHelper.generarCodigoProveedor())
        etCodigo.isEnabled = false

        // Adaptadores para los spinners
        val adapterTipoProveedor = ArrayAdapter.createFromResource(
            this,
            R.array.tipo_prov_opc,
            android.R.layout.simple_spinner_item
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spnTipoProvRP2.adapter = adapterTipoProveedor

        val adapterProvincias = ArrayAdapter.createFromResource(
            this,
            R.array.provincia_opc,
            android.R.layout.simple_spinner_item
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spnProvinciaRP2.adapter = adapterProvincias

        val adapterDistritos = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapterDistritos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnDistritoRP2.adapter = adapterDistritos

        spnProvinciaRP2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                adapterDistritos.add("Seleccionar distrito")
                if (idArrayDistritos != 0) {
                    val distritos = resources.getStringArray(idArrayDistritos)
                    adapterDistritos.addAll(*distritos)
                }
                spnDistritoRP2.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                adapterDistritos.clear()
                adapterDistritos.add("Seleccionar distrito")
                spnDistritoRP2.setSelection(0)
            }
        }

        btnSiguiente.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val tipo = spnTipoProvRP2.selectedItem.toString()
            val nombre = etNombre.text.toString()
            val direccion = etDireccion.text.toString()
            val provincia = spnProvinciaRP2.selectedItem.toString()
            val distrito = spnDistritoRP2.selectedItem.toString()
            val telefono = etTelefono.text.toString()
            val correo = etCorreo.text.toString()

            if (codigo.isBlank() || nombre.isBlank() || direccion.isBlank() || telefono.isBlank() || correo.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val proveedor = Proveedor(
                id = 0L,
                codigo = codigo,
                tipo = tipo,
                nombre = nombre,
                direccion = direccion,
                provincia = provincia,
                distrito = distrito,
                telefono = telefono,
                correo = correo,
                precio = 0.0
            )

            val idProveedor = dbHelper.insertarProveedor(proveedor)

            if (idProveedor != -1L) {
                dbHelper.registrarAuditoria(
                    usuario = usuarioActual,
                    rol = rolUsuario,
                    accion = "Registro",
                    entidad = "Proveedor",
                    detalle = "Proveedor registrado: $nombre ($codigo)"
                )

                Toast.makeText(this, "Proveedor registrado correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AgregarServicio::class.java)
                intent.putExtra("proveedorId", idProveedor)
                intent.putExtra("usuario", usuarioActual)
                intent.putExtra("rol", rolUsuario)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al registrar proveedor", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegresar.setOnClickListener {
            finish()
        }
    }
}
