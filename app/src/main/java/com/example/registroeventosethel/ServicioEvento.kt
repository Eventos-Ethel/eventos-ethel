package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ServicioEvento : AppCompatActivity() {
    private lateinit var dbHelper: SqliteHelper
    private lateinit var listView: ListView
    private lateinit var costoEvento: EditText
    private lateinit var selectedItems: MutableSet<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicio_evento)

        dbHelper = SqliteHelper(this)
        listView = findViewById(R.id.listView)
        costoEvento = findViewById(R.id.txndCostoEventoSE)
        selectedItems = mutableSetOf()

        val servicios = dbHelper.obtenerServicios()

        listView.adapter = object : BaseAdapter() {
            override fun getCount() = servicios.size
            override fun getItem(position: Int) = servicios[position]
            override fun getItemId(position: Int) = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
                val servicio = servicios[position]
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = "${servicio.nombre} - S/ ${servicio.costo}"
                listView.setItemChecked(position, selectedItems.contains(position))
                return view
            }
        }

        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val btnRegresarSE = findViewById<Button>(R.id.btnRegresarSE)
        val btnRegistrarSE = findViewById<Button>(R.id.btnRegistrarSE)

        btnRegresarSE.setOnClickListener {
            startActivity(Intent(this, RegistroEvento::class.java))
        }

        btnRegistrarSE.setOnClickListener {
            val checkedPositions = listView.checkedItemPositions
            val serviciosSeleccionados = mutableListOf<Servicio>()
            for (i in 0 until servicios.size) {
                if (checkedPositions.get(i)) {
                    serviciosSeleccionados.add(servicios[i])
                }
            }

            if (serviciosSeleccionados.isNotEmpty()) {
                val precio = costoEvento.text.toString().toDoubleOrNull() ?: 0.0

                // Guardar en la tabla de eventos (ajuste necesario para guardar múltiples servicios)
                val idEvento = dbHelper.registrarEvento(
                    intent.getStringExtra("nombre") ?: "",
                    intent.getStringExtra("dni") ?: "",
                    intent.getStringExtra("celular") ?: "",
                    intent.getStringExtra("descripcion") ?: "",
                    intent.getStringExtra("fecha") ?: "",
                    intent.getStringExtra("hora") ?: "",
                    intent.getStringExtra("tipo") ?: "",
                    intent.getIntExtra("invitados", 0),
                    intent.getStringExtra("ubicacion") ?: "",
                    precio,
                    -1 // Por ahora, guardamos sin servicio (lo haremos en una tabla extra)
                )

                if (idEvento != -1L) {
                    serviciosSeleccionados.forEach { servicio ->
                        dbHelper.insertarEventoServicio(idEvento, servicio.id)
                    }

                    Toast.makeText(this, "Evento registrado con servicios", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, PantallaPrincipal::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar evento", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Selecciona al menos un servicio", Toast.LENGTH_SHORT).show()
            }
        }
    }
}