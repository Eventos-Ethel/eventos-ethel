package com.example.registroeventosethel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventosAdapter(private var eventos: List<Evento>) :
    RecyclerView.Adapter<EventosAdapter.EventoViewHolder>() {

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreEvento: TextView = itemView.findViewById(R.id.tvNombreEvento)
        val textFecha: TextView = itemView.findViewById(R.id.textFecha)
        val textHora: TextView = itemView.findViewById(R.id.textHora)
        val textUbicacion: TextView = itemView.findViewById(R.id.textUbicacion)
        val textTipoEvento: TextView = itemView.findViewById(R.id.textTipoEvento)
        val tvDetalleEvento: TextView = itemView.findViewById(R.id.tvDetalleEvento)
        val textNombreCliente: TextView = itemView.findViewById(R.id.textNombreCliente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]

        // Mostrar el nombre del cliente arriba como título principal
        holder.tvNombreEvento.text = evento.nombreCliente

        // Llenar los campos visibles
        holder.textFecha.text = "📅 ${evento.fecha}"
        holder.textHora.text = "🕒 ${evento.hora}"
        holder.textUbicacion.text = "📍 ${evento.ubicacion}"
        holder.textTipoEvento.text = "🎯 ${evento.tipo}"

        // Formatear los detalles
        val precioFormateado = String.format("%.2f", evento.precio)
        holder.tvDetalleEvento.text = "💰 S/. $precioFormateado | 📞 ${evento.celular} | 🎉 ${evento.invitados} invitados"

        // Repetimos nombre del cliente como detalle inferior (opcional)
        holder.textNombreCliente.text = "👤 ${evento.nombreCliente}"
    }

    override fun getItemCount(): Int = eventos.size

    fun actualizarLista(nuevaLista: List<Evento>) {
        eventos = nuevaLista
        notifyDataSetChanged()
    }
}
