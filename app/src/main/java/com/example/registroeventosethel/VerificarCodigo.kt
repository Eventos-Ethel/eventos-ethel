package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class VerificarCodigo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificar_codigo)

        val etCodigo = findViewById<EditText>(R.id.etCodigoVerificacion)
        val btnVerificar = findViewById<Button>(R.id.btnVerificarCodigo)

        val codigoRecibido = intent.getStringExtra("codigo")
        val usuario = intent.getStringExtra("usuario")

        btnVerificar.setOnClickListener {
            val codigoIngresado = etCodigo.text.toString().trim()

            if (codigoIngresado == codigoRecibido) {
                Toast.makeText(this, "Código correcto", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NuevaContrasena::class.java)
                intent.putExtra("usuario", usuario)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
