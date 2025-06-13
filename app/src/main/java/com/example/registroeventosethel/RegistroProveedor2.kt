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
        etCodigo.isEnabled = false // para que no lo editen manualmente


        // Adaptador para el spinner de tipo de proveedor
        val adapterTipoProveedor = ArrayAdapter.createFromResource(
            this,
            R.array.tipo_prov_opc,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spnTipoProvRP2.adapter = adapterTipoProveedor

        // Adaptador para el spinner de provincias
        val adapterProvincias = ArrayAdapter.createFromResource(
            this,
            R.array.provincia_opc,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spnProvinciaRP2.adapter = adapterProvincias

        // Adaptador para el spinner de distritos (inicialmente vacío)
        val adapterDistritos = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapterDistritos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnDistritoRP2.adapter = adapterDistritos

        // Listener para el spinner de provincias
        spnProvinciaRP2.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtener la provincia seleccionada
                val provinciaSeleccionada = parent?.getItemAtPosition(position).toString()

                // Cargar los distritos correspondientes a la provincia seleccionada
                cargarDistritos(provinciaSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                adapterDistritos.clear()
                adapterDistritos.add("Seleccionar distrito")
                spnDistritoRP2.setSelection(0)
            }

            // Método para cargar los distritos según la provincia seleccionada
            private fun cargarDistritos(provincia: String) {
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

                // Cargar los distritos desde los recursos
                if (idArrayDistritos != 0) {
                    val distritos = resources.getStringArray(idArrayDistritos)
                    adapterDistritos.clear()
                    adapterDistritos.add("Seleccionar distrito")
                    adapterDistritos.addAll(*distritos) // Usamos * para descomponer el array
                } else {
                    adapterDistritos.clear()
                    adapterDistritos.add("Seleccionar distrito")
                }
                spnDistritoRP2.setSelection(0)
            }
        })

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
                id = 0L, // Se autogenera en la base de datos
                codigo = codigo,
                tipo = tipo,
                nombre = nombre,
                direccion = direccion,
                provincia = provincia,
                distrito = distrito,
                telefono = telefono,
                correo = correo,
                precio = 0.0 // ← el precio ya se gestiona por servicio
            )

            val idProveedor = dbHelper.insertarProveedor(proveedor)

            if (idProveedor != -1L) {
                Toast.makeText(this, "Proveedor registrado correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AgregarServicio::class.java)
                intent.putExtra("proveedorId", idProveedor)
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
