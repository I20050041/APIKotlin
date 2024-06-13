package com.example.ctrlinventario

import PApiService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ArticuloCRUD : ComponentActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextPrecio: EditText
    private lateinit var editTextCantidad: EditText
    private lateinit var btnSaveArticulo: Button
    private lateinit var btnUpdateArticulo: Button
    private lateinit var apiService: PApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.articulocrud)

        // Inicialización de vistas
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        editTextPrecio = findViewById(R.id.editTextPrecio)
        editTextCantidad = findViewById(R.id.editTextCantidad)
        btnSaveArticulo = findViewById(R.id.btnAddArticulo)
        btnUpdateArticulo = findViewById(R.id.btnUpdateArticulo)

        // Configuración de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7107/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(PApiService::class.java)

        // Recuperar el artículo seleccionado desde el intent
        val articulo: ArticuloP? = intent.getParcelableExtra("articulo")

        // Verificar si se recibió un artículo válido
        if (articulo != null) {
            // Mostrar los datos del artículo en los campos correspondientes
            editTextNombre.setText(articulo.nombre)
            editTextDescripcion.setText(articulo.descripcion)
            editTextPrecio.setText(articulo.precioUnitario.toString())
            editTextCantidad.setText(articulo.stock.toString())

            // Ocultar el botón de agregar (puede que no sea necesario)
            btnSaveArticulo.visibility = View.GONE

            // Configurar el botón de actualizar
            btnUpdateArticulo.setOnClickListener {
                val nombre = editTextNombre.text.toString()
                val descripcion = editTextDescripcion.text.toString()
                val precioUnitario = editTextPrecio.text.toString().toDoubleOrNull() ?: 0.0
                val stock = editTextCantidad.text.toString().toIntOrNull() ?: 0
                val estatus = 1 // Establecer el estatus a 1 por defecto

                val articuloActualizado = ArticuloP(articulo.idArticulo, nombre, descripcion, precioUnitario, stock, estatus)
                updateArticulo(articulo.idArticulo, articuloActualizado)
            }
        } else {
            // Ocultar el botón de actualizar
            btnUpdateArticulo.visibility = View.GONE

            // Configurar el botón de agregar
            btnSaveArticulo.setOnClickListener {
                val nombre = editTextNombre.text.toString()
                val descripcion = editTextDescripcion.text.toString()
                val precioUnitario = editTextPrecio.text.toString().toDoubleOrNull() ?: 0.0
                val stock = editTextCantidad.text.toString().toIntOrNull() ?: 0
                val estatus = 1 // Establecer el estatus a 1 por defecto

                val articuloNuevo = ArticuloP(0, nombre, descripcion, precioUnitario, stock, estatus)
                insertarArticulo(articuloNuevo)
            }
        }
    }

    private fun insertarArticulo(articulo: ArticuloP) {
        apiService.addArticulo(articulo).enqueue(object : Callback<List<ArticuloP>> {
            override fun onResponse(call: Call<List<ArticuloP>>, response: Response<List<ArticuloP>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ArticuloCRUD, "Artículo guardado con éxito", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ArticuloCRUD, MostrarArticulo::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ArticuloCRUD, "Error al guardar el artículo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ArticuloP>>, t: Throwable) {
                Toast.makeText(this@ArticuloCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para actualizar un artículo
    private fun updateArticulo(id: Int, articulo: ArticuloP) {
        val call = apiService.updateArticulo(id, articulo)
        call.enqueue(object : Callback<ArticuloP> {
            override fun onResponse(call: Call<ArticuloP>, response: Response<ArticuloP>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ArticuloCRUD, "Artículo modificado correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ArticuloCRUD, MostrarArticulo::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ArticuloCRUD, "Error al modificar el artículo: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("UpdateArticulo", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArticuloP>, t: Throwable) {
                Toast.makeText(this@ArticuloCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateArticulo", "Error de conexión", t)
            }
        })
    }

    private fun unsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val hostnameVerifier = HostnameVerifier { _, _ -> true }

            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier(hostnameVerifier)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}