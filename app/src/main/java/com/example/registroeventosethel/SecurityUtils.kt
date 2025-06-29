package com.example.registroeventosethel

import java.security.MessageDigest

fun hashContraseña(contraseña: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(contraseña.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}
