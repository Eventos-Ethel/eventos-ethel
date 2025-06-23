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
        val eventos = db.obtenerEventos().toMutableList()
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

                // Clic simple para editar
                view.setOnClickListener {
                    val intent = Intent(this@ListaEventos, EditarEvento::class.java)
                    intent.putExtra("evento", e)
                    startActivity(intent)
                }

// Pulsación larga para borrar
                view.setOnLongClickListener {
                    android.app.AlertDialog.Builder(this@ListaEventos)
                        .setTitle("Eliminar Evento")
                        .setMessage("¿Estás seguro de que quieres eliminar este evento?")
                        .setPositiveButton("Sí") { _, _ ->
                            val eventoEliminado = db.eliminarEvento(e.id)
                            if (eventoEliminado) {
                                db.registrarAuditoria(
                                    usuario = "admin", // Cambia esto por la variable de sesión si la tienes
                                    rol = "Administrador",
                                    accion = "Eliminar",
                                    entidad = "Evento",
                                    detalle = "Se eliminó el evento ID ${e.id} (${e.nombreCliente})"
                                )

                                eventos.removeAt(position)
                                notifyDataSetChanged()
                                android.widget.Toast.makeText(this@ListaEventos, "Evento eliminado", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                android.widget.Toast.makeText(this@ListaEventos, "Error al eliminar evento", android.widget.Toast.LENGTH_SHORT).show()
                            }

                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                    true

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