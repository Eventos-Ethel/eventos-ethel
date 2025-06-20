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

                //Usa los IDs de tu layout personalizado
                view.findViewById<TextView>(R.id.tvNombreProveedor).text = p.nombre
                view.findViewById<TextView>(R.id.tvServicioProveedor).text = "${p.tipo} - S/ ${p.precio}"
                view.findViewById<TextView>(R.id.tvTelefonoProveedor).text = "📞 ${p.telefono}"

                val btnEditar = view.findViewById<Button>(R.id.btnEditar)
                val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

                btnEditar.setOnClickListener {
                    val intent = Intent(this@ListaProveedores, EditarProveedores::class.java)
                    intent.putExtra("proveedor", p)
                    startActivity(intent)
                }

                btnEliminar.setOnClickListener {
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
