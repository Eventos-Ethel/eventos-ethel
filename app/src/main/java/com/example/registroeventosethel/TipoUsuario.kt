package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TipoUsuario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tipo_usuario)

        // Referencias a las imágenes
        val imageAdmin = findViewById<ImageView>(R.id.imageView3)
        val imageColaborador = findViewById<ImageView>(R.id.imageView2)

        imageAdmin.setOnClickListener {
            // Ir a la pantalla de Administrador
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        imageColaborador.setOnClickListener {
            // Ir a la pantalla de Colaborador
            val intent = Intent(this, ISAdmin::class.java)
            startActivity(intent)
        }
    }
}
