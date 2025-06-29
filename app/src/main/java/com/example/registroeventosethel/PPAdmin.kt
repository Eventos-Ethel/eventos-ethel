package com.example.registroeventosethel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class PPAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pp_admin)

        val imbRegProvPP = findViewById<ImageButton>(R.id.imbRegProvPP)
        val imbRegEventPP = findViewById<ImageButton>(R.id.imbRegEventPP)
        val imbListProvPP = findViewById<ImageButton>(R.id.imbListProvPP)
        val imbListEventPP = findViewById<ImageButton>(R.id.imbListEventPP)
        val imbCalendarioPP = findViewById<ImageButton>(R.id.imbCalendarioPP)
        val btnCerrarSesion = findViewById<MaterialButton>(R.id.btnCerrarSesionAdmin)
        val imbReportesAdmin = findViewById<ImageButton>(R.id.imbReportesAdm)

        imbRegProvPP.setOnClickListener{
            val siguiente = Intent(this, RegistroProveedor2::class.java)
            startActivity(siguiente)
        }
        imbRegEventPP.setOnClickListener{
            val siguiente = Intent(this, RegistroEvento::class.java)
            startActivity(siguiente)
        }
        imbListProvPP.setOnClickListener{
            val siguiente = Intent(this, ListaProveedores::class.java)
            startActivity(siguiente)
        }
        imbListEventPP.setOnClickListener{
            val siguiente = Intent(this, ListaEventos::class.java)
            startActivity(siguiente)
        }
        imbCalendarioPP.setOnClickListener{
            val siguiente = Intent(this, Calendario::class.java)
            startActivity(siguiente)
        }

        imbReportesAdmin.setOnClickListener{
            val reportes = Intent(this, AdminDashboard::class.java)
            startActivity(reportes)
        }
        btnCerrarSesion.setOnClickListener {
            val i = Intent(this, TipoUsuario::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }




    }
}