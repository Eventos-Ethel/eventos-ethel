package com.example.registroeventosethel

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroEvento : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_evento)

        val btnRegresarRE = findViewById<Button>(R.id.btnRegresarRE)
        val btnSiguienteRE = findViewById<Button>(R.id.btnSiguienteRE)

        btnRegresarRE.setOnClickListener{
            val siguiente = Intent(this, PantallaPrincipal::class.java)
            startActivity(siguiente)
        }

        btnSiguienteRE.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.txtNombreRE).text.toString()
            val dni = findViewById<EditText>(R.id.txnDniRE).text.toString()
            val celular = findViewById<EditText>(R.id.txnCelularRE).text.toString()
            val descripcion = findViewById<EditText>(R.id.txtDescripcionRE).text.toString()
            val fecha = findViewById<EditText>(R.id.txtFechaRE).text.toString()
            val hora = findViewById<EditText>(R.id.txtHoraRE).text.toString()
            val tipo = findViewById<EditText>(R.id.txtTipoRE).text.toString()
            val invitados = findViewById<EditText>(R.id.txnInvitadosRE).text.toString().toInt()
            val ubicacion = findViewById<EditText>(R.id.txtUbiRE).text.toString()

            val intent = Intent(this, ServicioEvento::class.java).apply {
                putExtra("nombre", nombre)
                putExtra("dni", dni)
                putExtra("celular", celular)
                putExtra("descripcion", descripcion)
                putExtra("fecha", fecha)
                putExtra("hora", hora)
                putExtra("tipo", tipo)
                putExtra("invitados", invitados)
                putExtra("ubicacion", ubicacion)
            }
            startActivity(intent)
        }




    }
}