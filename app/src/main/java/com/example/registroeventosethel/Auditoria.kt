package com.example.registroeventosethel

data class Auditoria(
    val usuario: String,
    val rol: String,
    val accion: String,
    val entidad: String,
    val detalle: String,
    val fechaHora: String
)
