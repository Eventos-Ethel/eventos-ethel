package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ListaProveedores : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_proveedores)

        val db = SqliteHelper(this)
        val proveedores = db.obtenerProveedores()
        val btnRegresarLP = findViewById<Button>(R.id.btnRegresarLP)
        val gridView = findViewById<GridView>(R.id.gridViewProveedores)

        val adapter = object : BaseAdapter() {
            override fun getCount() = proveedores.size
            override fun getItem(position: Int) = proveedores[position]
            override fun getItemId(position: Int) = proveedores[position].id

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.item_proveedor, parent, false)
                val p = proveedores[position]

                val servicio = db.obtenerServicioPorProveedor(p.id)
                val tipoServicio = servicio?.tipo ?: "Sin servicio"
                val precioServicio = if (servicio != null) "S/ ${servicio.costo}" else "S/ 0.00"

                view.findViewById<TextView>(R.id.tvNombreProveedor).text = p.nombre
                view.findViewById<TextView>(R.id.tvServicioProveedor).text = "$tipoServicio - $precioServicio"
                view.findViewById<TextView>(R.id.tvTelefonoProveedor).text = "📞 ${p.telefono}"

                view.findViewById<Button>(R.id.btnEditar).setOnClickListener {
                    val intent = Intent(this@ListaProveedores, EditarProveedores::class.java)
                    intent.putExtra("proveedor", p)
                    startActivity(intent)
                }

                view.findViewById<Button>(R.id.btnEliminar).setOnClickListener {
                    db.eliminarProveedor(p.id)
                    (proveedores as MutableList).removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(this@ListaProveedores, "Proveedor eliminado", Toast.LENGTH_SHORT).show()
                }

                return view
            }
        }

        gridView.adapter = adapter

        btnRegresarLP.setOnClickListener {
            startActivity(Intent(this, PantallaPrincipal::class.java))
        }
    }
}
