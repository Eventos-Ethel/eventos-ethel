package com.example.registroeventosethel

import java.io.Serializable

class Usuario(private var nombreCompleto: String, private var nombreUsuario: String, private var contraseña: String, val esAdmin: Boolean = false): Serializable
{
    var NombreCompleto: String
        set (value) { nombreCompleto = value }
        get() { return nombreCompleto }

    var NombreUsuario: String
        set(value) { nombreUsuario = value }
        get() { return nombreUsuario }

    var Contraseña: String
        set (value) { contraseña = value }
        get() { return contraseña }

}