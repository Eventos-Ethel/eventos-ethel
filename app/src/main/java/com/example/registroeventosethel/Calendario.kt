package com.example.registroeventosethel

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import android.graphics.Color
import android.widget.Button
import com.example.registroeventosethel.R.id.btnRegresarPP
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class Calendario : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var spinnerUbicacion: Spinner
    private lateinit var editTextLugar: EditText
    private lateinit var editTextCliente: EditText
    private lateinit var editTextFechaInicio: EditText
    private lateinit var editTextFechaFin: EditText
    private lateinit var btnFiltrar: Button
    private lateinit var recyclerViewEventos: RecyclerView
    private lateinit var eventosAdapter: EventosAdapter
    private lateinit var db: SqliteHelper

    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val fechasConEventos = mutableSetOf<CalendarDay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        val btnRegresarPP = findViewById<Button>(btnRegresarPP)
        calendarView = findViewById(R.id.calendarView)
        spinnerUbicacion = findViewById(R.id.spinnerUbicacion)
        editTextLugar = findViewById(R.id.editTextLugar)
        editTextCliente = findViewById(R.id.editTextCliente)
        editTextFechaInicio = findViewById(R.id.editTextFechaInicio)
        editTextFechaFin = findViewById(R.id.editTextFechaFin)
        btnFiltrar = findViewById(R.id.btnFiltrar)
        recyclerViewEventos = findViewById(R.id.recyclerViewEventos)

        db = SqliteHelper(this)

        eventosAdapter = EventosAdapter(emptyList())
        recyclerViewEventos.layoutManager = LinearLayoutManager(this)
        recyclerViewEventos.adapter = eventosAdapter

        cargarUbicaciones()

        editTextFechaInicio.setOnClickListener { mostrarDatePicker(editTextFechaInicio) }
        editTextFechaFin.setOnClickListener { mostrarDatePicker(editTextFechaFin) }

        btnFiltrar.setOnClickListener { aplicarFiltros() }

        calendarView.setOnDateChangedListener { _, date, _ ->
            val fechaSeleccionada = String.format("%02d/%02d/%d", date.day, date.month, date.year)
            val eventosDelDia = db.obtenerEventos().filter { it.fecha == fechaSeleccionada }
            eventosAdapter.actualizarLista(eventosDelDia)
            if (eventosDelDia.isEmpty()) {
                Toast.makeText(this, "No hay eventos para esta fecha", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegresarPP.setOnClickListener {
            val siguiente = Intent(this, PantallaPrincipal::class.java)
            startActivity(siguiente)
        }

        marcarFechasConEventos()
        eventosAdapter.actualizarLista(db.obtenerEventos())
    }

    private fun mostrarDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _: DatePicker, year: Int, month: Int, day: Int ->
            val fecha = Calendar.getInstance()
            fecha.set(year, month, day)
            editText.setText(formatoFecha.format(fecha.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun cargarUbicaciones() {
        val ubicaciones = mutableListOf("Todos")
        ubicaciones.addAll(db.obtenerLugaresUnicos())
        spinnerUbicacion.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ubicaciones)
    }

    private fun aplicarFiltros() {
        val lugar = editTextLugar.text.toString().takeIf { it.isNotBlank() }
        val cliente = editTextCliente.text.toString().takeIf { it.isNotBlank() }
        val fechaInicio = editTextFechaInicio.text.toString().takeIf { it.isNotBlank() }
        val fechaFin = editTextFechaFin.text.toString().takeIf { it.isNotBlank() }
        val ubicacion = spinnerUbicacion.selectedItem.toString().takeIf { it != "Todos" }

        try {
            val inicioDate = fechaInicio?.let { formatoFecha.parse(it) }
            val finDate = fechaFin?.let { formatoFecha.parse(it) }

            val eventosFiltrados = db.obtenerEventosFiltrados(
                fechaInicio = inicioDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                fechaFin = finDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                lugar = ubicacion,
                nombreCliente = cliente
            )

            eventosAdapter.actualizarLista(eventosFiltrados)
            marcarFechasConEventos()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al filtrar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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