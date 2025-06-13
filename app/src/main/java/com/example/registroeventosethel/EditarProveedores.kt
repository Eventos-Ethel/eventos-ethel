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

        // Cargar datos en los campos
        etCodigo.setText(proveedor.codigo)
        etNombre.setText(proveedor.nombre)
        etDireccion.setText(proveedor.direccion)
        etCorreo.setText(proveedor.correo)
        etTelefono.setText(proveedor.telefono)

        // Provincias y distritos (puedes adaptar según tu lógica real)
        val provincias = listOf("Cañete", "Lima", "Ica")
        val distritos = listOf("San Vicente", "Imperial", "Nuevo Imperial")

        spinnerProvincia.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, provincias)
        spinnerDistrito.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, distritos)

        spinnerProvincia.setSelection(provincias.indexOf(proveedor.provincia))
        spinnerDistrito.setSelection(distritos.indexOf(proveedor.distrito))

        // Tipos de servicio
        val tipos = listOf("Persona", "Empresa")
        val tipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)
        spinnerTipo.adapter = tipoAdapter
        val tipoSeleccionado = tipos.indexOf(proveedor.tipo)
        if (tipoSeleccionado >= 0) spinnerTipo.setSelection(tipoSeleccionado)

        btnRegresarEP.setOnClickListener{
            startActivity(Intent(this, ListaProveedores::class.java))
        }
        // Guardar cambios
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
                Toast.makeText(this, "Proveedor actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar proveedor", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
