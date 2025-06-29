package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditarServicioProv : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_servicio) // Se reutiliza layout

        val db = SqliteHelper(this)
        val proveedorId = intent.getLongExtra("proveedorId", -1L)

        //Usuario actual
        val usuarioActual = intent.getStringExtra("usuario") ?: "desconocido"
        val rolUsuario = intent.getStringExtra("rol") ?: "desconocido"

        val servicio = db.obtenerServicioPorProveedor(proveedorId)
        val etCodigo = findViewById<EditText>(R.id.txnCodigoSP)
        val etNombre = findViewById<EditText>(R.id.txtNombreSP)
        val etTipo = findViewById<EditText>(R.id.txtTipoSP)
        val etCosto = findViewById<EditText>(R.id.txnCostoSP)
        val btnGuardar = findViewById<Button>(R.id.btnRegistrarSP)
        val btnRegresar = findViewById<Button>(R.id.btnRegresarSP)

        // Mostrar datos existentes
        if (servicio != null) {
            etCodigo.setText(servicio.codigo)
            etCodigo.isEnabled = false
            etNombre.setText(servicio.nombre)
            etTipo.setText(servicio.tipo)
            etCosto.setText(servicio.costo.toString())
        } else {
            etCodigo.setText(db.generarCodigoServicio())
            etCodigo.isEnabled = false
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val tipo = etTipo.text.toString()
            val costo = etCosto.text.toString().toDoubleOrNull() ?: 0.0

            if (nombre.isBlank() || tipo.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (servicio != null) {
                val actualizado = db.actualizarServicio(servicio.id, nombre, tipo, costo)
                if (actualizado) {
                    //Registrar modificación en la auditoría
                    db.registrarAuditoria(
                        usuario = usuarioActual,
                        rol = rolUsuario,
                        accion = "Modificación",
                        entidad = "Servicio",
                        detalle = "Servicio actualizado: ${servicio.codigo} - $nombre"
                    )

                    Toast.makeText(this, "Servicio actualizado correctamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ListaProveedores::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } else {
                val codigo = etCodigo.text.toString()
                val registrado = db.registrarServicio(proveedorId, codigo, nombre, tipo, costo)
                if (registrado) {
                    //Registrar creación en la auditoría
                    db.registrarAuditoria(
                        usuario = usuarioActual,
                        rol = rolUsuario,
                        accion = "Registro",
                        entidad = "Servicio",
                        detalle = "Servicio registrado: $codigo - $nombre"
                    )

                    Toast.makeText(this, "Servicio registrado correctamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ListaProveedores::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRegresar.setOnClickListener {
            finish()
        }
    }
}
