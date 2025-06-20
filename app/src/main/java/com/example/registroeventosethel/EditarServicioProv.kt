package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditarServicioProv : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_servicio) // Puedes reutilizar el layout existente

        val proveedorId = intent.getLongExtra("proveedorId", -1L)
        val db = SqliteHelper(this)

        val servicio = db.obtenerServicioPorProveedor(proveedorId)
        val etCodigo = findViewById<EditText>(R.id.txnCodigoSP)
        val etNombre = findViewById<EditText>(R.id.txtNombreSP)
        val etTipo = findViewById<EditText>(R.id.txtTipoSP)
        val etCosto = findViewById<EditText>(R.id.txnCostoSP)
        val btnGuardar = findViewById<Button>(R.id.btnRegistrarSP)
        val btnRegresar = findViewById<Button>(R.id.btnRegresarSP)

        // Rellenar campos si ya hay un servicio
        if (servicio != null) {
            etCodigo.setText(servicio.codigo)
            etCodigo.isEnabled = false
            etNombre.setText(servicio.nombre)
            etTipo.setText(servicio.tipo)
            etCosto.setText(servicio.costo.toString())
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val tipo = etTipo.text.toString()
            val costo = etCosto.text.toString().toDoubleOrNull() ?: 0.0

            if (servicio != null) {
                val actualizado = db.actualizarServicio(
                    servicio.id, nombre, tipo, costo
                )
                if (actualizado) {
                    Toast.makeText(this, "Servicio actualizado correctamente", Toast.LENGTH_SHORT).show()
                    val i = Intent(this, ListaProveedores::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Si no hay servicio, se registra uno nuevo
                val nuevo = db.registrarServicio(proveedorId, etCodigo.text.toString(), nombre, tipo, costo)
                if (nuevo) {
                    Toast.makeText(this, "Servicio registrado correctamente", Toast.LENGTH_SHORT).show()
                    val i = Intent(this, ListaProveedores::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRegresar.setOnClickListener { finish() }
    }
}
