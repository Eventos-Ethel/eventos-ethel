package com.example.registroeventosethel

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class JavaMailAPI(
    private val context: Context,
    private val email: String,
    private val subject: String,
    private val message: String
) : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void?): Boolean {
        val properties = Properties()
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.starttls.enable"] = "true"
        properties["mail.smtp.host"] = "smtp.gmail.com"
        properties["mail.smtp.port"] = "587"

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("tucorreo@gmail.com", "tu_contraseña_o_app_password")
            }
        })

        return try {
            val mimeMessage = MimeMessage(session)
            mimeMessage.setFrom(InternetAddress("tucorreo@gmail.com"))
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
            mimeMessage.subject = subject
            mimeMessage.setText(message)
            Transport.send(mimeMessage)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        Toast.makeText(
            context,
            if (result) "Correo enviado exitosamente" else "Error al enviar el correo",
            Toast.LENGTH_LONG
        ).show()
    }
}
