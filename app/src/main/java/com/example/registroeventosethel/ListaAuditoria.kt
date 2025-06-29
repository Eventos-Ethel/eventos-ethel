package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ListaAuditoria : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_auditoria)

        val db = SqliteHelper(this)
        val registros = db.obtenerAuditoria()
        val listView = findViewById<ListView>(R.id.listViewAuditoria)
        val btnRegresar = findViewById<Button>(R.id.btnRegresarAuditoria)

        val adapter = object : BaseAdapter() {
            override fun getCount() = registros.size
            override fun getItem(position: Int) = registros[position]
            override fun getItemId(position: Int) = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false)
                val item = registros[position]

                view.findViewById<TextView>(android.R.id.text1).text =
                    "[${item.fechaHora}] ${item.usuario} (${item.rol})"

                view.findViewById<TextView>(android.R.id.text2).text =
                    "${item.accion} ${item.entidad} → ${item.detalle}"

                return view
            }
        }

        listView.adapter = adapter

        btnRegresar.setOnClickListener {
            startActivity(Intent(this, PantallaPrincipal::class.java))
            finish()
        }
    }
}
