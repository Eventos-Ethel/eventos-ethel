package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BienvenidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        val nombreUsuario = intent.getStringExtra("usuario") ?: "Usuario"

        val txtBienvenida = findViewById<TextView>(R.id.txtBienvenida)
        txtBienvenida.alpha = 0f
        txtBienvenida.animate().alpha(1f).setDuration(800).start()
        txtBienvenida.text = "✨ ¡Hola, $nombreUsuario!"

        // Esperar 1 segundos y redirigir
        Handler(Looper.getMainLooper()).postDelayed({
            val rol = intent.getStringExtra("rol") ?: "Colaborador"
            val intentPrincipal = Intent(this, PantallaPrincipal::class.java)
            intentPrincipal.putExtra("usuario", nombreUsuario)
            intentPrincipal.putExtra("rol", rol)
            startActivity(intentPrincipal)
            finish()
        }, 1000)
    }
}
