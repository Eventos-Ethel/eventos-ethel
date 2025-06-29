package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class NuevaContrasena : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_contrasena)

        val etNuevaContrasena = findViewById<EditText>(R.id.etNuevaContrasena)
        val etConfirmarContrasena = findViewById<EditText>(R.id.etConfirmarContrasena)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarContrasena)

        val usuario = intent.getStringExtra("usuario")

        btnGuardar.setOnClickListener {
            val nueva = etNuevaContrasena.text.toString().trim()
            val confirmar = etConfirmarContrasena.text.toString().trim()

            if (nueva.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nueva != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = SqliteHelper(this)
            val contraseñaHash = hashContraseña(nueva)
            db.actualizarContraseña(usuario!!, contraseñaHash)

            Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
            finish()
        }
    }
}
