package com.example.registroeventosethel

import java.io.Serializable

data class Proveedor(
    val id: Long,
    val codigo: String,
    val tipo: String,
    val nombre: String,
    val direccion: String,
    val provincia: String,
    val distrito: String,
    val telefono: String,
    val correo: String,
    val precio: Double
) : Serializable
