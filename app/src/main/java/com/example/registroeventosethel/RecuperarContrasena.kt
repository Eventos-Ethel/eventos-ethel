package com.example.registroeventosethel

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RecuperarContrasena : AppCompatActivity() {

    private val correoRemitente = "2201010335@undc.edu.pe"
    private val contraseñaRemitente = "efwidlikpkajqpmn" // Usa contraseña de app

    private var codigoGenerado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        val etNombreUsuario = findViewById<EditText>(R.id.etNombreUsuario)
        val etCorreo = findViewById<EditText>(R.id.etCorreoElectronico)
        val btnRecuperar = findViewById<Button>(R.id.btnRecuperar)

        val progressDialog = ProgressDialog(this).apply {
            setMessage("Enviando código de recuperación...")
            setCancelable(false)
        }

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
                codigoGenerado = (100000..999999).random().toString()

                val mensajeCorreo = """
                    Hola, $usuarioTexto 👋

                    Recibimos una solicitud para restablecer tu contraseña.
                    Tu código de verificación es: $codigoGenerado

                    Este código expirará en 5 minutos.

                    Si no solicitaste esto, puedes ignorar este mensaje.

                    Saludos cordiales,  
                    RegistroEventosEthel
                """.trimIndent()

                progressDialog.show()

                Thread {
                    val emailSender = EmailSender(correoRemitente, contraseñaRemitente)
                    val exito = emailSender.enviarCorreo(
                        destinatario = correoDestino,
                        asunto = "Código de recuperación",
                        mensaje = mensajeCorreo
                    )

                    runOnUiThread {
                        progressDialog.dismiss()
                        if (exito) {
                            Toast.makeText(this, "Código enviado, revisa tu correo", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, VerificarCodigo::class.java)
                            intent.putExtra("codigo", codigoGenerado)
                            intent.putExtra("usuario", usuario.NombreUsuario)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
