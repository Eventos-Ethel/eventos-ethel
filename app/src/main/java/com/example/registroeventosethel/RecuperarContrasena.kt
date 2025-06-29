package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class RecuperarContrasena : AppCompatActivity() {

    private val correoRemitente = "2201010335@undc.edu.pe"
    private val contraseñaRemitente = "efwidlikpkajqpmn" // usa contraseña de app, no tu contraseña real

    private var codigoGenerado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        val etNombreUsuario = findViewById<EditText>(R.id.etNombreUsuario)
        val etCorreo = findViewById<EditText>(R.id.etCorreoElectronico)
        val btnRecuperar = findViewById<Button>(R.id.btnRecuperar)

        btnRecuperar.setOnClickListener {
            val usuarioTexto = etNombreUsuario.text.toString().trim()
            val correoDestino = etCorreo.text.toString().trim()

            if (usuarioTexto.isEmpty() || correoDestino.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = SqliteHelper(this)
            val usuario = db.obtenerUsuario(usuarioTexto)

            if (usuario != null) {
                // Generar código aleatorio
                codigoGenerado = (100000..999999).random().toString()

                val emailSender = EmailSender(correoRemitente, contraseñaRemitente)
                val exito = emailSender.enviarCorreo(
                    destinatario = correoDestino,
                    asunto = "Código de recuperación",
                    mensaje = "Tu código de recuperación es: $codigoGenerado"
                )

                if (exito) {
                    Toast.makeText(this, "Código enviado, revisa tu correo", Toast.LENGTH_LONG).show()

                    // Redirigir a pantalla de verificación
                    val intent = Intent(this, VerificarCodigo::class.java)
                    intent.putExtra("codigo", codigoGenerado)
                    intent.putExtra("usuario", usuario.NombreUsuario)
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
