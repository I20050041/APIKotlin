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

class ProveedorAdapter(
    private val context: Context,
    private val proveedores: List<ProveedorP>,
    private val apiService: PApiService
) : BaseAdapter() {

    override fun getCount(): Int = proveedores.size

    override fun getItem(position: Int): Any = proveedores[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_proveedor, parent, false)

        val txtProveedor = view.findViewById<TextView>(R.id.txtProveedor)
        val btnModificar = view.findViewById<Button>(R.id.btnModificar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

        val proveedor = proveedores[position]

        txtProveedor.text = "Nombre: ${proveedor.nombre}, Razón Social: ${proveedor.razonSocial}, Dirección: ${proveedor.direccion}, Teléfono: ${proveedor.telefono}, RFC: ${proveedor.rfc}, Correo: ${proveedor.correo}"

        btnModificar.setOnClickListener {
            val intent = Intent(context, ProveedorCRUD::class.java)
            intent.putExtra("proveedor", proveedor)
            context.startActivity(intent)
        }

        btnEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro de que deseas eliminar este proveedor?")

            builder.setPositiveButton("Sí") { dialog, which ->
                val call = apiService.inactivarProveedor(proveedor.idProveedor)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Proveedor inactivado correctamente", Toast.LENGTH_SHORT).show()
                            // Actualizar la lista de proveedores después de eliminar
                            (context as MostrarProveedor).obtenerTodosLosProveedores()
                        } else {
                            Toast.makeText(context, "Error al inactivar el proveedor", Toast.LENGTH_SHORT).show()
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
