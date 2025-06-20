package com.example.registroeventosethel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroEvento : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_evento)

        val extras = intent.extras
        extras?.let {
            findViewById<EditText>(R.id.txtNombreRE).setText(it.getString("nombre", ""))
            findViewById<EditText>(R.id.txnDniRE).setText(it.getString("dni", ""))
            findViewById<EditText>(R.id.txnCelularRE).setText(it.getString("celular", ""))
            findViewById<EditText>(R.id.txtDescripcionRE).setText(it.getString("descripcion", ""))
            findViewById<EditText>(R.id.txtFechaRE).setText(it.getString("fecha", ""))
            findViewById<EditText>(R.id.txtHoraRE).setText(it.getString("hora", ""))
            findViewById<EditText>(R.id.txtTipoRE).setText(it.getString("tipo", ""))
            findViewById<EditText>(R.id.txnInvitadosRE).setText(it.getInt("invitados", 0).toString())
            findViewById<EditText>(R.id.txtUbiRE).setText(it.getString("ubicacion", ""))
        }


        val btnRegresarRE = findViewById<Button>(R.id.btnRegresarRE)
        val btnSiguienteRE = findViewById<Button>(R.id.btnSiguienteRE)
        val txtFechaRE = findViewById<EditText>(R.id.txtFechaRE)
        val txtHoraRE = findViewById<EditText>(R.id.txtHoraRE)

        btnRegresarRE.setOnClickListener{
            val siguiente = Intent(this, PantallaPrincipal::class.java)
            startActivity(siguiente)
        }

        txtFechaRE.setOnClickListener {
            val calendario = Calendar.getInstance()
            val año = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                txtFechaRE.setText(fechaFormateada)
        }, año, mes, dia)
            datePicker.show()
            }

        txtHoraRE.setOnClickListener {
            val calendario = Calendar.getInstance()
            val hora = calendario.get(Calendar.HOUR_OF_DAY)
            val minuto = calendario.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }
                val formato = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val horaFormateada = formato.format(calendar.time)
                txtHoraRE.setText(horaFormateada)
            }, hora, minuto, false) // false = formato 12 horas

            timePicker.show()
        }

        btnSiguienteRE.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.txtNombreRE).text.toString()
            val dni = findViewById<EditText>(R.id.txnDniRE).text.toString()
            val celular = findViewById<EditText>(R.id.txnCelularRE).text.toString()
            val descripcion = findViewById<EditText>(R.id.txtDescripcionRE).text.toString()
            val fecha = findViewById<EditText>(R.id.txtFechaRE).text.toString()
            val hora = findViewById<EditText>(R.id.txtHoraRE).text.toString()
            val tipo = findViewById<EditText>(R.id.txtTipoRE).text.toString()
            val invitados = findViewById<EditText>(R.id.txnInvitadosRE).text.toString().toInt()
            val ubicacion = findViewById<EditText>(R.id.txtUbiRE).text.toString()

            val intent = Intent(this, ServicioEvento::class.java).apply {
                putExtra("nombre", nombre)
                putExtra("dni", dni)
                putExtra("celular", celular)
                putExtra("descripcion", descripcion)
                putExtra("fecha", fecha)
                putExtra("hora", hora)
                putExtra("tipo", tipo)
                putExtra("invitados", invitados)
                putExtra("ubicacion", ubicacion)
            }
            startActivity(intent)
        }




    }
}