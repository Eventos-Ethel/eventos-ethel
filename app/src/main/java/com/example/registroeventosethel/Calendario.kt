package com.example.registroeventosethel

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

class Calendario : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var db: SqliteHelper

    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val fechasConEventos = mutableSetOf<CalendarDay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        calendarView = findViewById(R.id.calendarView)
        val btnRegresar = findViewById<Button>(R.id.btnRegresarPP)
        val btnFiltradoCliente = findViewById<Button>(R.id.btnIrAFiltradoCliente)
        val btnFiltradoFecha = findViewById<Button>(R.id.btnIrAFiltradoFecha)

        db = SqliteHelper(this)

        // Mostrar detalles al hacer clic en una fecha
        calendarView.setOnDateChangedListener { _, date, _ ->
            val fechaSeleccionada = String.format("%02d/%02d/%d", date.day, date.month, date.year)
            val eventosDelDia = db.obtenerEventos().filter { it.fecha == fechaSeleccionada }

            if (eventosDelDia.isNotEmpty()) {
                val detalles = eventosDelDia.joinToString("\n\n") { evento ->
                    """
                        📌 Cliente: ${evento.nombreCliente}
                        📍 Lugar: ${evento.ubicacion}
                        ⏰ Hora: ${evento.hora}
                        📞 Celular: ${evento.celular}
                    """.trimIndent()
                }

                AlertDialog.Builder(this)
                    .setTitle("Eventos del $fechaSeleccionada")
                    .setMessage(detalles)
                    .setPositiveButton("Cerrar", null)
                    .show()
            } else {
                Toast.makeText(this, "No hay eventos para esta fecha", Toast.LENGTH_SHORT).show()
            }
        }

        btnFiltradoCliente.setOnClickListener {
            startActivity(Intent(this, CalendarioFiltradoCliente::class.java))
        }

        btnFiltradoFecha.setOnClickListener {
            startActivity(Intent(this, CalendarioFiltradoFecha::class.java))
        }

        btnRegresar.setOnClickListener {
            startActivity(Intent(this, PantallaPrincipal::class.java))
        }

        marcarFechasConEventos()
    }

    private fun marcarFechasConEventos() {
        fechasConEventos.clear()
        for (evento in db.obtenerEventos()) {
            try {
                val fecha = formatoFecha.parse(evento.fecha)
                val calendar = Calendar.getInstance().apply { time = fecha!! }
                fechasConEventos.add(
                    CalendarDay.from(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        calendarView.removeDecorators()
        calendarView.addDecorator(EventDecorator(Color.RED, fechasConEventos.toHashSet()))
    }
}
