package com.example.registroeventosethel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RevisorEventosReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val db = SqliteHelper(context)
        val eventos = db.obtenerEventos()

        val hoy = LocalDate.now()
        val mañana = hoy.plusDays(1)
        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val eventosProximos = eventos.filter {
            val fechaEvento = LocalDate.parse(it.fecha, formato)
            fechaEvento == hoy || fechaEvento == mañana
        }

        if (eventosProximos.isNotEmpty()) {
            val mensaje = "Tienes ${eventosProximos.size} evento(s) programado(s) para hoy o mañana"
            NotificacionUtils.mostrarNotificacion(
                context = context,
                titulo = "Recordatorio de eventos",
                mensaje = mensaje
            )
        }
    }
}
