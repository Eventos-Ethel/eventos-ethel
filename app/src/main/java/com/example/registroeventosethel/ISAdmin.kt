package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ISAdmin : AppCompatActivity() {

    private lateinit var txtUsuario: EditText
    private lateinit var txtContraseña: EditText
    private lateinit var btnAceptar: Button
    private lateinit var btnRegresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        txtUsuario = findViewById(R.id.txtUserNameIS)
        txtContraseña = findViewById(R.id.txtContraseñaIS)
        btnAceptar = findViewById(R.id.btnAceptarIS)
        btnRegresar = findViewById(R.id.btnRegresarIS)

        btnAceptar.setOnClickListener {
            val usuarioTexto = txtUsuario.text.toString()
            val contraseñaTexto = txtContraseña.text.toString()
            val db = SqliteHelper(this)
            val usuario = db.obtenerUsuario(usuarioTexto)

            if (usuario != null && usuario.Contraseña == contraseñaTexto) {
                if (!usuario.esAdmin) {
                    Toast.makeText(this, "Este usuario no es administrador", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                Toast.makeText(this, "Bienvenido Administrador", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, PPAdmin::class.java))
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }


        btnRegresar.setOnClickListener {
            val regresar = Intent(this, TipoUsuario::class.java)
            startActivity(regresar)
        }
    }
}
