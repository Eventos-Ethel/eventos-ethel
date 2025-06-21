package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegistrarUsuario : AppCompatActivity() {

    private var editTextNombreCompleto: EditText? = null
    private var editTextNombreUsuario: EditText? = null
    private var editTextContraseña: EditText? = null
    private var editTextConfirmarContraseña: EditText? = null
    private var btnAceptarReg: Button? = null
    private var btnRegresarReg: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        editTextNombreCompleto = findViewById(R.id.txtNomComp)
        editTextNombreUsuario = findViewById(R.id.txtUserName)
        editTextContraseña = findViewById(R.id.txtContraseña)
        editTextConfirmarContraseña = findViewById(R.id.txtConfirmarContra)
        btnAceptarReg = findViewById(R.id.btnAceptarReg)
        btnRegresarReg = findViewById(R.id.btnRegresarReg)

        btnAceptarReg?.setOnClickListener {
            registrarUsuario()
        }

        btnRegresarReg?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun registrarUsuario() {
        val nombreCompleto = editTextNombreCompleto?.text?.toString()?.trim()
        val nombreUsuario = editTextNombreUsuario?.text?.toString()?.trim()
        val contraseña = editTextContraseña?.text?.toString()
        val confirmarContraseña = editTextConfirmarContraseña?.text?.toString()

        if (nombreCompleto.isNullOrBlank() || nombreUsuario.isNullOrBlank() || contraseña.isNullOrBlank() || confirmarContraseña.isNullOrBlank()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (contraseña != confirmarContraseña) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        if (!esContraseñaValida(contraseña)) {
            Toast.makeText(
                this,
                "La contraseña debe tener al menos una mayúscula, una minúscula y un número",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val dbHelper = SqliteHelper(this)

        if (dbHelper.usuarioExiste(nombreUsuario)) {
            Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = Usuario(nombreCompleto, nombreUsuario, contraseña)
        if (dbHelper.registrarUsuario(usuario)) {
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun esContraseñaValida(contraseña: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")
        return regex.matches(contraseña)
    }
}
