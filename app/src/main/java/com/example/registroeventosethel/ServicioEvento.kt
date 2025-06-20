package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ServicioEvento : AppCompatActivity() {
    private lateinit var dbHelper: SqliteHelper
    private lateinit var listView: ListView
    private lateinit var costoEvento: EditText
    private lateinit var txvCostoSE: TextView
    private lateinit var selectedItems: MutableSet<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicio_evento)

        dbHelper = SqliteHelper(this)
        listView = findViewById(R.id.listView)
        costoEvento = findViewById(R.id.txndCostoEventoSE)
        txvCostoSE = findViewById(R.id.txvCostoSE)
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

        // Actualizar el total cuando se seleccione un servicio
        listView.setOnItemClickListener { _, _, _, _ ->
            actualizarCostoTotal(servicios)
        }

        // También actualizar cuando se escriba el costo propio
        costoEvento.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                actualizarCostoTotal(servicios)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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
                val precioUsuario = costoEvento.text.toString().toDoubleOrNull() ?: 0.0
                val sumaServicios = serviciosSeleccionados.sumOf { it.costo }
                val total = precioUsuario + sumaServicios

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
                    total,
                    -1
                )

                if (idEvento != -1L) {
                    serviciosSeleccionados.forEach { servicio ->
                        dbHelper.insertarEventoServicio(idEvento, servicio.id)
                    }

                    Toast.makeText(this, "Evento registrado con éxito", Toast.LENGTH_SHORT).show()
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

    private fun actualizarCostoTotal(servicios: List<Servicio>) {
        val checkedPositions = listView.checkedItemPositions
        var sumaServicios = 0.0
        for (i in 0 until servicios.size) {
            if (checkedPositions.get(i)) {
                sumaServicios += servicios[i].costo
            }
        }

        val costoPropio = costoEvento.text.toString().toDoubleOrNull() ?: 0.0
        val total = sumaServicios + costoPropio

        txvCostoSE.text = "S/ %.2f".format(total)
    }
}
