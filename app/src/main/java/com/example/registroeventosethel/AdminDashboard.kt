package com.example.registroeventosethel

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import android.content.Intent
import android.widget.Button
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class AdminDashboard : AppCompatActivity() {

    private lateinit var dbHelper: SqliteHelper
    private lateinit var tvEventosSemana: TextView
    private lateinit var tvEventosMes: TextView
    private lateinit var tvTotalIngresos: TextView
    private val CREATE_PDF_REQUEST_CODE = 1002
    private var pdfContent: ByteArray? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        dbHelper = SqliteHelper(this)

        tvEventosSemana = findViewById(R.id.tvEventosSemana)
        tvEventosMes = findViewById(R.id.tvEventosMes)
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos)

        val btnDescargarPdf = findViewById<Button>(R.id.btnDescargarPdf)
        btnDescargarPdf.setOnClickListener {
            generarInformePDF()
        }

        mostrarInformes()
        val btnVerAuditoria = findViewById<Button>(R.id.btnVerAuditoria)
        btnVerAuditoria.setOnClickListener {
            val intent = Intent(this, ListaAuditoria::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CREATE_PDF_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    val outputStream = contentResolver.openOutputStream(uri)
                    outputStream?.write(pdfContent)
                    outputStream?.close()
                    Toast.makeText(this, "PDF guardado correctamente", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al guardar el PDF", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun generarInformePDF() {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val eventos = dbHelper.obtenerEventos()

        // Crear la primera página
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        var page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        paint.textSize = 16f
        var y = 40f
        canvas.drawText("📊 Informe General del Administrador", 40f, y, paint)

        paint.textSize = 14f
        y += 30f
        canvas.drawText(tvEventosSemana.text.toString(), 40f, y, paint)
        y += 20f
        canvas.drawText(tvEventosMes.text.toString(), 40f, y, paint)
        y += 20f
        canvas.drawText(tvTotalIngresos.text.toString(), 40f, y, paint)
        y += 40f

        // Título de la tabla
        paint.textSize = 15f
        canvas.drawText("📋 Detalle de Eventos Registrados:", 40f, y, paint)
        y += 30f

        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText("Cliente", 40f, y, paint)
        canvas.drawText("Fecha", 160f, y, paint)
        canvas.drawText("S/.", 240f, y, paint)
        canvas.drawText("Cel.", 300f, y, paint)
        canvas.drawText("DNI", 420f, y, paint)

        paint.isFakeBoldText = false
        y += 20f

        // Mostrar cada evento
        for ((index, evento) in eventos.withIndex()) {
            if (y > 780f) {  // Nueva página si se pasa
                pdfDocument.finishPage(page)
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, index + 2).create()
                page = pdfDocument.startPage(pageInfo)
                y = 40f
            }

            canvas.drawText(evento.nombreCliente, 40f, y, paint)
            canvas.drawText(evento.fecha, 160f, y, paint)
            canvas.drawText("%.2f".format(evento.precio), 240f, y, paint)
            canvas.drawText(evento.celular, 300f, y, paint)
            canvas.drawText(evento.dni, 420f, y, paint)
            y += 18f
        }

        pdfDocument.finishPage(page)

        // Guardar en memoria para permitir al usuario escoger ubicación
        val outputStream = java.io.ByteArrayOutputStream()
        pdfDocument.writeTo(outputStream)
        pdfContent = outputStream.toByteArray()
        pdfDocument.close()

        // Lanzar selector para guardar
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "InformeAdmin.pdf")
        }
        startActivityForResult(intent, CREATE_PDF_REQUEST_CODE)
    }



}
