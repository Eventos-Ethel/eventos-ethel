package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditarEvento : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_evento)

        val evento = intent.getSerializableExtra("evento") as Evento
        val db = SqliteHelper(this)

        val txtNombre = findViewById<EditText>(R.id.txtNombreEE)
        val txtDni = findViewById<EditText>(R.id.editTextNumber)
        val txtCelular = findViewById<EditText>(R.id.editTextNumber2)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcionEE)
        val txtFecha = findViewById<EditText>(R.id.txtFechaEE)
        val txtHora = findViewById<EditText>(R.id.txtHoraEE)
        val txtTipo = findViewById<EditText>(R.id.txtTipoEE)
        val txtInvitados = findViewById<EditText>(R.id.txnInvitadosEE)
        val txtUbicacion = findViewById<EditText>(R.id.txtUbiEE)
        val btnGuardar = findViewById<Button>(R.id.btnSiguienteEE)
        val btnRegresar = findViewById<Button>(R.id.btnRegresarEE)

        // Cargar datos
        txtNombre.setText(evento.nombreCliente)
        txtDni.setText(evento.dni)
        txtCelular.setText(evento.celular)
        txtDescripcion.setText(evento.descripcion)
        txtFecha.setText(evento.fecha)
        txtHora.setText(evento.hora)
        txtTipo.setText(evento.tipo)
        txtInvitados.setText(evento.invitados.toString())
        txtUbicacion.setText(evento.ubicacion)

        btnRegresar.setOnClickListener {
            val regresar = Intent(this, ListaEventos::class.java)
            startActivity(regresar)
        }

        btnGuardar.setOnClickListener {
            val actualizado = db.actualizarEvento(
                evento.id,
                txtNombre.text.toString(),
                txtDni.text.toString(),
                txtCelular.text.toString(),
                txtDescripcion.text.toString(),
                txtFecha.text.toString(),
                txtHora.text.toString(),
                txtTipo.text.toString(),
                txtInvitados.text.toString().toInt(),
                txtUbicacion.text.toString(),
                evento.precio
            )

            if (actualizado) {
                Toast.makeText(this, "Evento actualizado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
    }

}