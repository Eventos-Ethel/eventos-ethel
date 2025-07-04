package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ListaProveedoresAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_proveedores)

        val db = SqliteHelper(this)
        val proveedores = db.obtenerProveedores().toMutableList()
        val btnRegresarLP = findViewById<Button>(R.id.btnRegresarLP)
        val gridView = findViewById<GridView>(R.id.gridViewProveedores)

        //Recuperar usuario actual
        val usuario = intent.getStringExtra("usuario") ?: "desconocido"
        val rol = intent.getStringExtra("rol") ?: "desconocido"

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
                    val intent = Intent(this@ListaProveedoresAdmin, EditarProveedoresAdmin::class.java)
                    intent.putExtra("proveedor", p)
                    intent.putExtra("usuario", usuario)
                    intent.putExtra("rol", rol)
                    startActivity(intent)
                }

                view.findViewById<Button>(R.id.btnEliminar).setOnClickListener {
                    AlertDialog.Builder(this@ListaProveedoresAdmin)
                        .setTitle("¿Eliminar proveedor?")
                        .setMessage("¿Deseas eliminar este proveedor y su servicio asociado?")
                        .setPositiveButton("Sí") { _, _ ->
                            val eliminado = db.eliminarProveedor(p.id)
                            if (eliminado) {
                                db.registrarAuditoria(
                                    usuario = usuario,
                                    rol = rol,
                                    accion = "Eliminación",
                                    entidad = "Proveedor",
                                    detalle = "Proveedor eliminado: ${p.nombre} (${p.codigo})"
                                )

                                proveedores.removeAt(position)
                                notifyDataSetChanged()
                                Toast.makeText(this@ListaProveedoresAdmin, "Proveedor eliminado", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@ListaProveedoresAdmin, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }

                return view
            }
        }

        gridView.adapter = adapter

        btnRegresarLP.setOnClickListener {
            val intent = Intent(this, PPAdmin::class.java)
            intent.putExtra("usuario", usuario)
            intent.putExtra("rol", rol)
            startActivity(intent)
        }
    }
}
