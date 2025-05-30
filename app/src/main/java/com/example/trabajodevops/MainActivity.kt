package com.example.trabajodevops

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnInicioSesion = findViewById<Button>(R.id.btnInicioSesion)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnInicioSesion.setOnClickListener{
            val siguiente =Intent(this, IniciarSesion::class.java)
            startActivity(siguiente)
        }
        btnRegistrar.setOnClickListener{
            val siguiente = Intent(this, RegistrarUsuario::class.java)
            startActivity(siguiente)
        }
    }
}