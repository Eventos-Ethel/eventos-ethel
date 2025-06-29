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
    private val MAX_INTENTOS = 3
    private val TIEMPO_BLOQUEO_MS = 30_000L // 30 segundos

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
            val prefs = getSharedPreferences("sesion_admin", MODE_PRIVATE)

            val intentosFallidos = prefs.getInt("intentos_fallidos_admin", 0)
            val tiempoBloqueo = prefs.getLong("bloqueo_hasta_admin", 0L)
            val ahora = System.currentTimeMillis()

            if (ahora < tiempoBloqueo) {
                Toast.makeText(this, "Acceso bloqueado. Intenta nuevamente en unos segundos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = SqliteHelper(this)
            val usuario = db.obtenerUsuario(usuarioTexto)
            val contraseñaEncriptada = hashContraseña(contraseñaTexto)

            if (usuario != null && usuario.Contraseña == contraseñaEncriptada) {
                if (!usuario.esAdmin) {
                    Toast.makeText(this, "Este usuario no es administrador", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Login exitoso: limpiar intentos fallidos
                prefs.edit()
                    .putInt("intentos_fallidos_admin", 0)
                    .remove("bloqueo_hasta_admin")
                    .putString("usuario", usuario.NombreUsuario)
                    .putString("rol", "Administrador")
                    .apply()

                Toast.makeText(this, "Bienvenido Administrador", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, PPAdmin::class.java)
                intent.putExtra("usuario", usuario.NombreUsuario)
                intent.putExtra("rol", "Administrador")
                startActivity(intent)
            } else {
                val nuevosIntentos = intentosFallidos + 1
                val editor = prefs.edit()
                editor.putInt("intentos_fallidos_admin", nuevosIntentos)

                if (nuevosIntentos >= MAX_INTENTOS) {
                    editor.putLong("bloqueo_hasta_admin", ahora + TIEMPO_BLOQUEO_MS)
                    Toast.makeText(this, "Demasiados intentos. Acceso bloqueado por 30 segundos.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Credenciales incorrectas ($nuevosIntentos/$MAX_INTENTOS)", Toast.LENGTH_SHORT).show()
                }

                editor.apply()
            }
        }

        btnRegresar.setOnClickListener {
            val regresar = Intent(this, TipoUsuario::class.java)
            startActivity(regresar)
        }
    }
}
