package com.example.registroeventosethel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.registroeventosethel.hashContraseña

class IniciarSesion : AppCompatActivity() {

    private var editTextNombreUsuario: EditText? = null
    private var editTextContraseña: EditText? = null
    private var btnAceptarIS: Button? = null
    private var btnRegresarIS: Button? = null
    private val MAX_INTENTOS = 5
    private val TIEMPO_BLOQUEO_MS = 30_000L // 30 segundos


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val usuarioGuardado = prefs.getString("usuario", null)
        val rolGuardado = prefs.getString("rol", null)

        if (usuarioGuardado != null && rolGuardado != null) {
            Toast.makeText(this, "Sesión restaurada automáticamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PantallaPrincipal::class.java)
            intent.putExtra("usuario", usuarioGuardado)
            intent.putExtra("rol", rolGuardado)
            startActivity(intent)
            finish()
            return
        }

        editTextNombreUsuario = findViewById(R.id.txtUserNameIS)
        editTextContraseña = findViewById(R.id.txtContraseñaIS)
        btnAceptarIS = findViewById(R.id.btnAceptarIS)
        btnRegresarIS = findViewById(R.id.btnRegresarIS)

        btnAceptarIS?.setOnClickListener {
            iniciarSesion()
        }
        btnRegresarIS?.setOnClickListener {
            val siguiente = Intent(this, MainActivity::class.java)
            startActivity(siguiente)
        }



        val tvOlvidasteContrasena = findViewById<TextView>(R.id.tvOlvidasteContrasena)
        tvOlvidasteContrasena.setOnClickListener {
            val intent = Intent(this, RecuperarContrasena::class.java)
            startActivity(intent)
        }

    }

    private fun iniciarSesion() {
        val nombreUsuario = editTextNombreUsuario?.text?.toString()
        val contraseña = editTextContraseña?.text?.toString()
        val esAdminEsperado = intent.getBooleanExtra("esAdmin", false)
        val chkMantenerSesion = findViewById<CheckBox>(R.id.chkMantenerSesion)

        if (nombreUsuario.isNullOrBlank() || contraseña.isNullOrBlank()) {
            Toast.makeText(this, "Por favor completar todos los datos", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val intentosFallidos = prefs.getInt("intentos_fallidos", 0)
        val tiempoBloqueo = prefs.getLong("bloqueo_hasta", 0L)
        val ahora = System.currentTimeMillis()

        if (ahora < tiempoBloqueo) {
            Toast.makeText(this, "Acceso bloqueado. Intenta más tarde.", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = SqliteHelper(this)
        val usuario = dbHelper.obtenerUsuario(nombreUsuario)

        val contraseñaEncriptada = hashContraseña(contraseña)

        if (usuario != null && usuario.Contraseña == contraseñaEncriptada) {
            if (usuario.esAdmin != esAdminEsperado) {
                Toast.makeText(this, "Solo colaboradores", Toast.LENGTH_SHORT).show()
                return
            }

            // Éxito: limpiar intentos fallidos
            prefs.edit()
                .putInt("intentos_fallidos", 0)
                .remove("bloqueo_hasta")
                .apply()

            // Guardar sesión solo si marcó el CheckBox
            val chkMantenerSesion = findViewById<CheckBox>(R.id.chkMantenerSesion)
            if (chkMantenerSesion.isChecked) {
                prefs.edit()
                    .putString("usuario", usuario.NombreUsuario)
                    .putString("rol", if (usuario.esAdmin) "Administrador" else "Colaborador")
                    .apply()
            }

            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PantallaPrincipal::class.java)
            intent.putExtra("usuario", usuario.NombreUsuario)
            intent.putExtra("rol", "Colaborador")
            startActivity(intent)
        }
        else {
            val nuevosIntentos = intentosFallidos + 1
            val editor = prefs.edit()
            editor.putInt("intentos_fallidos", nuevosIntentos)

            if (nuevosIntentos >= MAX_INTENTOS) {
                editor.putLong("bloqueo_hasta", ahora + TIEMPO_BLOQUEO_MS)
                Toast.makeText(this, "Demasiados intentos. Acceso bloqueado 30 segundos.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrecta ($nuevosIntentos/$MAX_INTENTOS)", Toast.LENGTH_SHORT).show()
            }
            editor.apply()
        }


    }


}