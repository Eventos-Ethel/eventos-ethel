package com.example.registroeventosethel

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class CalendarioFiltradoFecha : AppCompatActivity() {

    private lateinit var editTextFechaInicio: EditText
    private lateinit var editTextFechaFin: EditText
    private lateinit var btnFiltrar: Button
    private lateinit var btnRegresar: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventosAdapter
    private lateinit var db: SqliteHelper

    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_filtrado_fecha)

        editTextFechaInicio = findViewById(R.id.editTextFechaInicio)
        editTextFechaFin = findViewById(R.id.editTextFechaFin)
        btnFiltrar = findViewById(R.id.btnFiltrar)
        btnRegresar = findViewById(R.id.btnRegresarEF2)
        recyclerView = findViewById(R.id.recyclerViewEventosFecha)

        db = SqliteHelper(this)

        adapter = EventosAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        editTextFechaInicio.setOnClickListener { mostrarDatePicker(editTextFechaInicio) }
        editTextFechaFin.setOnClickListener { mostrarDatePicker(editTextFechaFin) }

        btnFiltrar.setOnClickListener {
            aplicarFiltroPorFechas()
        }

        btnRegresar.setOnClickListener {
            startActivity(Intent(this, Calendario::class.java))
        }
    }

    private fun mostrarDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            calendar.set(year, month, day)
            editText.setText(formatoFecha.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun aplicarFiltroPorFechas() {
        val fechaInicioStr = editTextFechaInicio.text.toString()
        val fechaFinStr = editTextFechaFin.text.toString()

        try {
            val fechaInicio = formatoFecha.parse(fechaInicioStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val fechaFin = formatoFecha.parse(fechaFinStr)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

            val eventos = db.obtenerEventosFiltrados(
                fechaInicio = fechaInicio,
                fechaFin = fechaFin
            )

            adapter.actualizarLista(eventos)

            if (eventos.isEmpty()) {
                Toast.makeText(this, "No se encontraron eventos en el rango de fechas", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error en las fechas: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
