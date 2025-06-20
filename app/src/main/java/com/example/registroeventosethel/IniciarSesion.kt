package com.example.registroeventosethel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IniciarSesion : AppCompatActivity() {

    private var editTextNombreUsuario: EditText? = null
    private var editTextContraseña: EditText? = null
    private var btnAceptarIS: Button? = null
    private var btnRegresarIS: Button? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)


        editTextNombreUsuario = findViewById(R.id.txtUserNameIS)
        editTextContraseña = findViewById(R.id.txtContraseñaIS)
        btnAceptarIS = findViewById(R.id.btnAceptarIS)
        btnRegresarIS = findViewById(R.id.btnRegresarIS)

        btnAceptarIS?.setOnClickListener{
            iniciarSesion()
        }
        btnRegresarIS?.setOnClickListener{
            val siguiente = Intent(this, MainActivity::class.java)
            startActivity(siguiente)
        }

        val tvOlvidasteContrasena = findViewById<TextView>(R.id.tvOlvidasteContrasena)
        tvOlvidasteContrasena.setOnClickListener {
            val intent = Intent(this, RecuperarContrasena::class.java)
            startActivity(intent)
        }

    }

    private fun iniciarSesion(){
        val nombreUsuario = editTextNombreUsuario?.text?.toString()
        val contraseña = editTextContraseña?.text?.toString()

        if (nombreUsuario.isNullOrBlank() || contraseña.isNullOrBlank()){
            Toast.makeText(this, "Por favor completar todos los datos", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = SqliteHelper(this)
        val usuario = dbHelper.obtenerUsuario(nombreUsuario)

        if (usuario != null && usuario.Contraseña == contraseña) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            val siguiente = Intent(this, PantallaPrincipal::class.java)
            startActivity(siguiente)
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show()
        }
    }
}