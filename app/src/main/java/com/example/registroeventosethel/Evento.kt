package com.example.registroeventosethel

import java.io.Serializable

data class Evento(
    val id: Long,
    val nombreCliente: String,
    val fecha: String, //formato dd/MM/yyyy
    val hora: String,
    val precio: Double,
    val celular: String,
    val invitados: Int,
    val ubicacion: String,
    val tipo: String,
    val descripcion: String,
    val dni: String
): Serializable