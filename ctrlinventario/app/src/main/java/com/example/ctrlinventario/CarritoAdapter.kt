package com.example.ctrlinventario

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarritoAdapter(
    private val context: Context,
    private val carrito: MutableList<ArticuloP>,
    private val onUpdateTotal: () -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.txtNombreCarrito)
        val precioTextView: TextView = view.findViewById(R.id.txtPrecioCarrito)
        val eliminarButton: Button = view.findViewById(R.id.btnEliminarCarrito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val articulo = carrito[position]
        holder.nombreTextView.text = articulo.nombre
        holder.precioTextView.text = "Precio: ${articulo.precioUnitario}"

        holder.eliminarButton.setOnClickListener {
            carrito.removeAt(position)
            notifyDataSetChanged()
            onUpdateTotal()
        }
    }

    override fun getItemCount(): Int = carrito.size
}
