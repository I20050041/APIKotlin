package com.example.ctrlinventario

import PApiService
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticuloAdapter(
    private val context: Context,
    private val articulos: List<ArticuloP>,
    private val apiService: PApiService,
    private val carrito: MutableList<ArticuloP>
) : BaseAdapter() {

    override fun getCount(): Int = articulos.size

    override fun getItem(position: Int): Any = articulos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_articulo, parent, false)

        val txtArticulo = view.findViewById<TextView>(R.id.txtArticulo)
        val btnModificar = view.findViewById<Button>(R.id.btnModificar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)
        val btnAgregarCarrito = view.findViewById<Button>(R.id.btnAgregarCarrito)

        val articulo = articulos[position]

        txtArticulo.text = "Nombre: ${articulo.nombre}, Descripción: ${articulo.descripcion}, Precio: ${articulo.precioUnitario}, Stock: ${articulo.stock}"

        btnModificar.setOnClickListener {
            val intent = Intent(context, ArticuloCRUD::class.java)
            intent.putExtra("articulo", articulo)
            context.startActivity(intent)
        }

        btnEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro de que deseas eliminar este artículo?")

            builder.setPositiveButton("Sí") { dialog, which ->
                val call = apiService.inactivarArticulo(articulo.idArticulo)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Artículo inactivado correctamente", Toast.LENGTH_SHORT).show()
                            (context as MostrarArticulo).obtenerTodosLosArticulos()
                        } else {
                            Toast.makeText(context, "Error al inactivar el artículo", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                // No hacer nada, simplemente cerrar el diálogo
            }

            val dialog = builder.create()
            dialog.show()
        }

        btnAgregarCarrito.setOnClickListener {
            carrito.add(articulo)
            Toast.makeText(context, "Artículo agregado al carrito", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}