package com.example.registroeventosethel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditarProveedores : AppCompatActivity() {

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

        val provincias = listOf("Cañete", "Lima", "Ica")
        val distritos = listOf("San Vicente", "Imperial", "Nuevo Imperial")

        spinnerProvincia.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, provincias)
        spinnerDistrito.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, distritos)

        spinnerProvincia.setSelection(provincias.indexOf(proveedor.provincia))
        spinnerDistrito.setSelection(distritos.indexOf(proveedor.distrito))

        val tipos = listOf("Persona", "Empresa")
        val tipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)
        spinnerTipo.adapter = tipoAdapter
        spinnerTipo.setSelection(tipos.indexOf(proveedor.tipo))

        btnRegresarEP.setOnClickListener {
            startActivity(Intent(this, ListaProveedores::class.java))
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
                val intent = Intent(this, EditarServicioProv::class.java)
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
