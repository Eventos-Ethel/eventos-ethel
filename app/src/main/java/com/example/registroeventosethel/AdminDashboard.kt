package com.example.registroeventosethel

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import android.content.Intent
import android.widget.Button

class AdminDashboard : AppCompatActivity() {

    private lateinit var dbHelper: SqliteHelper
    private lateinit var tvEventosSemana: TextView
    private lateinit var tvEventosMes: TextView
    private lateinit var tvTotalIngresos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        dbHelper = SqliteHelper(this)

        tvEventosSemana = findViewById(R.id.tvEventosSemana)
        tvEventosMes = findViewById(R.id.tvEventosMes)
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos)

        mostrarInformes()
        val btnVerAuditoria = findViewById<Button>(R.id.btnVerAuditoria)
        btnVerAuditoria.setOnClickListener {
            val intent = Intent(this, ListaAuditoria::class.java)
            startActivity(intent)
        }

    }

    private fun mostrarInformes() {
        val eventos = dbHelper.obtenerEventos()

        val hoy = LocalDate.now()
        val primerDiaSemana = hoy.with(java.time.DayOfWeek.MONDAY)
        val ultimoDiaSemana = hoy.with(java.time.DayOfWeek.SUNDAY)
        val mesActual = YearMonth.from(hoy)

        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        var eventosSemana = 0
        var eventosMes = 0
        var ingresosMes = 0.0

        for (evento in eventos) {
            val fecha = LocalDate.parse(evento.fecha, formato)

            if (!fecha.isBefore(primerDiaSemana) && !fecha.isAfter(ultimoDiaSemana)) {
                eventosSemana++
            }

            if (YearMonth.from(fecha) == mesActual) {
                eventosMes++
                ingresosMes += evento.precio
            }
        }

        tvEventosSemana.text = "Eventos esta semana: $eventosSemana"
        tvEventosMes.text = "Eventos este mes: $eventosMes"
        tvTotalIngresos.text = "Ingresos totales del mes: S/ %.2f".format(ingresosMes)
    }

}
