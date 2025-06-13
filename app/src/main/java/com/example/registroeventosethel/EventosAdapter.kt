package com.example.registroeventosethel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class EventosAdapter(private var eventos: List<Evento>) :
    RecyclerView.Adapter<EventosAdapter.EventoViewHolder>() {

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreCliente: TextView = itemView.findViewById(R.id.textNombreCliente)
        val fecha: TextView = itemView.findViewById(R.id.textFecha)
        val hora: TextView = itemView.findViewById(R.id.textHora)
        val ubicacion: TextView = itemView.findViewById(R.id.textUbicacion)
        val tipo: TextView = itemView.findViewById(R.id.textTipoEvento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.nombreCliente.text = evento.nombreCliente
        holder.fecha.text = evento.fecha
        holder.hora.text = evento.hora
        holder.ubicacion.text = evento.ubicacion
        holder.tipo.text = evento.tipo
    }

    override fun getItemCount(): Int = eventos.size

    fun actualizarLista(nuevaLista: List<Evento>) {
        eventos = nuevaLista
        notifyDataSetChanged()
    }
}
