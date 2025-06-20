package com.example.registroeventosethel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Servicio(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val tipo: String,
    val costo: Double,
    val idProveedor: Long
): Parcelable