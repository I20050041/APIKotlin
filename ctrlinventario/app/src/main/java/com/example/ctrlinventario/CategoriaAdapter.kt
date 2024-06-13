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

class CategoriaAdapter(
    private val context: Context,
    private val categorias: List<CategoriaP>,
    private val apiService: PApiService
) : BaseAdapter() {

    override fun getCount(): Int = categorias.size

    override fun getItem(position: Int): Any = categorias[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false)

        val txtCategoria = view.findViewById<TextView>(R.id.txtCategoria)
        val btnModificar = view.findViewById<Button>(R.id.btnModificar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

        val categoria = categorias[position]

        txtCategoria.text = "Nombre: ${categoria.nombre}, Descripción: ${categoria.descripcion}"

        btnModificar.setOnClickListener {
            val intent = Intent(context, CategoriaCRUD::class.java)
            intent.putExtra("categoria", categoria)
            context.startActivity(intent)
        }

        btnEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro de que deseas eliminar esta categoría?")

            builder.setPositiveButton("Sí") { dialog, which ->
                val call = apiService.inactivarCategoria(categoria.idCategoria)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Categoría inactivada correctamente", Toast.LENGTH_SHORT).show()
                            (context as MostrarCategoria).obtenerTodosLasCategorias()
                        } else {
                            Toast.makeText(context, "Error al inactivar la categoría", Toast.LENGTH_SHORT).show()
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

        return view
    }
}
