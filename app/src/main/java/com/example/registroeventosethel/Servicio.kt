package com.example.registroeventosethel

data class Servicio(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val tipo: String,
    val costo: Double,
    val idProveedor: Long
)