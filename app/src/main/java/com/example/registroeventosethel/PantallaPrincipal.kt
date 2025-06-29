package com.example.registroeventosethel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PantallaPrincipal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_prinicipal)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        val imbRegProvPP = findViewById<ImageButton>(R.id.imbRegProvPP)
        val imbRegEventPP = findViewById<ImageButton>(R.id.imbRegEventPP)
        val imbListProvPP = findViewById<ImageButton>(R.id.imbListProvPP)
        val imbListEventPP = findViewById<ImageButton>(R.id.imbListEventPP)
        val imbCalendarioPP = findViewById<ImageButton>(R.id.imbCalendarioPP)

        val usuario = intent.getStringExtra("usuario") ?: "desconocido"
        val rol = intent.getStringExtra("rol") ?: "desconocido"

        imbRegProvPP.setOnClickListener {
            val intent = Intent(this, RegistroProveedor2::class.java)
            intent.putExtra("usuario", usuario)
            intent.putExtra("rol", rol)
            startActivity(intent)
        }

        imbRegEventPP.setOnClickListener {
            val intent = Intent(this, RegistroEvento::class.java)
            intent.putExtra("usuario", usuario)
            intent.putExtra("rol", rol)
            startActivity(intent)
        }

        imbListProvPP.setOnClickListener {
            startActivity(Intent(this, ListaProveedores::class.java))
        }

        imbListEventPP.setOnClickListener {
            startActivity(Intent(this, ListaEventos::class.java))
        }

        imbCalendarioPP.setOnClickListener {
            startActivity(Intent(this, Calendario::class.java))
        }

        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        btnCerrarSesion.setOnClickListener {
            val i = Intent(this, TipoUsuario::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        // Crear canal de notificación
        NotificacionUtils.crearCanal(this)

        // Verificar eventos para notificar si hay alguno hoy o mañana
        verificarEventosProximos()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(this, RevisorEventosReceiver::class.java)
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 8) // Hora deseada (8:00 AM)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }

        alarmManager.setRepeating(
            android.app.AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            android.app.AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }

    private fun verificarEventosProximos() {
        val db = SqliteHelper(this)
        val eventos = db.obtenerEventos()

        val hoy = LocalDate.now()
        val mañana = hoy.plusDays(1)
        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val eventosProximos = eventos.filter {
            val fechaEvento = LocalDate.parse(it.fecha, formato)
            fechaEvento == hoy || fechaEvento == mañana
        }

        if (eventosProximos.isNotEmpty()) {
            val mensaje = "Tienes ${eventosProximos.size} evento(s) programado(s) para hoy o mañana"
            NotificacionUtils.mostrarNotificacion(
                context = this,
                titulo = "Recordatorio de eventos",
                mensaje = mensaje
            )
        }
    }
}
