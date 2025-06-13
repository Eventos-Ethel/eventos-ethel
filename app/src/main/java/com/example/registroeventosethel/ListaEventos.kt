package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListaEventos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_eventos)

        val db = SqliteHelper(this)
        val eventos = db.obtenerEventos()
        val btnRegresarLE = findViewById<Button>(R.id.btnRegresarLE)


        val gridView = findViewById<GridView>(R.id.gridViewEventos)
        val adapter = object : BaseAdapter() {
            override fun getCount() = eventos.size
            override fun getItem(position: Int) = eventos[position]
            override fun getItemId(position: Int) = eventos[position].id
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = convertView ?: layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false)
                val e = eventos[position]
                view.findViewById<TextView>(android.R.id.text1).text = "${e.nombreCliente} - ${e.fecha} ${e.hora}"
                view.findViewById<TextView>(android.R.id.text2).text = "S/ ${e.precio} | ${e.celular} | ${e.invitados} invitados"

                view.setOnClickListener {
                    val intent = Intent(this@ListaEventos, EditarEvento::class.java)
                    intent.putExtra("evento", e) // Evento implementa Serializable
                    startActivity(intent)
                }

                return view
            }
        }

        btnRegresarLE.setOnClickListener{
            val siguiente = Intent(this, PantallaPrincipal::class.java)
            startActivity(siguiente)
        }

        gridView.adapter = adapter
    }
}