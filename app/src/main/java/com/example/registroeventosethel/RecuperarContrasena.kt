package com.example.registroeventosethel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RecuperarContrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        val etNombreUsuario = findViewById<EditText>(R.id.etNombreUsuario)
        val btnRecuperar = findViewById<Button>(R.id.btnRecuperar)

        btnRecuperar.setOnClickListener {
            val nombreUsuario = etNombreUsuario.text.toString().trim()
            if (nombreUsuario.isNotEmpty()) {
                val dbHelper = SqliteHelper(this)
                val usuario = dbHelper.obtenerUsuario(nombreUsuario)
                if (usuario != null) {
                    Toast.makeText(
                        this,
                        "Tu contraseña es: ${usuario.Contraseña}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ingresa tu nombre de usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
