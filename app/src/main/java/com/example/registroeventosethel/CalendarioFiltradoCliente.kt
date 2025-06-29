package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CalendarioFiltradoCliente : AppCompatActivity() {

    private lateinit var editTextBuscarCliente: EditText
    private lateinit var recyclerViewEventos: RecyclerView
    private lateinit var btnRegresar: Button

    private lateinit var db: SqliteHelper
    private lateinit var eventosAdapter: EventosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_filtrado_cliente)

        editTextBuscarCliente = findViewById(R.id.editTextBuscarCliente)
        recyclerViewEventos = findViewById(R.id.recyclerViewEventosFiltrados)
        btnRegresar = findViewById(R.id.btnRegresarEF1)

        db = SqliteHelper(this)
        eventosAdapter = EventosAdapter(emptyList())
        recyclerViewEventos.layoutManager = LinearLayoutManager(this)
        recyclerViewEventos.adapter = eventosAdapter

        // Escucha los cambios de texto automáticamente
        editTextBuscarCliente.setOnEditorActionListener { v, _, _ ->
            val nombre = v.text.toString().trim()
            buscarEventosPorCliente(nombre)
            true
        }

        btnRegresar.setOnClickListener {
            startActivity(Intent(this, Calendario::class.java))
        }
    }

    private fun buscarEventosPorCliente(nombre: String) {
        if (nombre.isNotEmpty()) {
            val eventos = db.obtenerEventosFiltrados(nombreCliente = nombre)
            if (eventos.isNotEmpty()) {
                eventosAdapter.actualizarLista(eventos)
            } else {
                Toast.makeText(this, "No se encontraron eventos", Toast.LENGTH_SHORT).show()
                eventosAdapter.actualizarLista(emptyList())
            }
        } else {
            Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_SHORT).show()
        }
    }
}
