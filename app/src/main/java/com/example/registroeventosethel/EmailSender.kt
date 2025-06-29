package com.example.registroeventosethel

import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender(private val correoRemitente: String, private val contraseñaRemitente: String) {

    fun enviarCorreo(destinatario: String, asunto: String, mensaje: String): Boolean {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(correoRemitente, contraseñaRemitente)
            }
        })

        return try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(correoRemitente))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario))
                subject = asunto
                setText(mensaje)
            }

            Thread {
                Transport.send(message)
            }.start()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
