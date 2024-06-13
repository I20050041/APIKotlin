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

class CategoriaCRUD : ComponentActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var btnSaveCategoria: Button
    private lateinit var btnUpdateCategoria: Button
    private lateinit var apiService: PApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categoriacrud)

        // Inicialización de vistas
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        btnSaveCategoria = findViewById(R.id.btnAddCategoria)
        btnUpdateCategoria = findViewById(R.id.btnUpdateCategoria)

        // Configuración de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7107/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(PApiService::class.java)

        // Recuperar la categoría seleccionada desde el intent
        val categoria: CategoriaP? = intent.getParcelableExtra("categoria")

        // Verificar si se recibió una categoría válida
        if (categoria != null) {
            // Mostrar los datos de la categoría en los campos correspondientes
            editTextNombre.setText(categoria.nombre)
            editTextDescripcion.setText(categoria.descripcion)

            // Ocultar el botón de agregar (puede que no sea necesario)
            btnSaveCategoria.visibility = View.GONE

            // Configurar el botón de actualizar
            btnUpdateCategoria.setOnClickListener {
                val nombre = editTextNombre.text.toString()
                val descripcion = editTextDescripcion.text.toString()
                val estatus = 1 // Establecer el estatus a 1 por defecto

                val categoriaActualizada = CategoriaP(categoria.idCategoria, nombre, descripcion, estatus)
                updateCategoria(categoria.idCategoria, categoriaActualizada)
            }
        } else {
            // Ocultar el botón de actualizar
            btnUpdateCategoria.visibility = View.GONE

            // Configurar el botón de agregar
            btnSaveCategoria.setOnClickListener {
                val nombre = editTextNombre.text.toString()
                val descripcion = editTextDescripcion.text.toString()
                val estatus = 1 // Establecer el estatus a 1 por defecto

                val categoriaNueva = CategoriaP(0, nombre, descripcion, estatus)
                insertarCategoria(categoriaNueva)
            }
        }
    }

    private fun insertarCategoria(categoria: CategoriaP) {
        apiService.addCategoria(categoria).enqueue(object : Callback<List<CategoriaP>> {
            override fun onResponse(call: Call<List<CategoriaP>>, response: Response<List<CategoriaP>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CategoriaCRUD, "Categoría guardada con éxito", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CategoriaCRUD, MostrarCategoria::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@CategoriaCRUD, "Error al guardar la categoría", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CategoriaP>>, t: Throwable) {
                Toast.makeText(this@CategoriaCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para actualizar una categoría
    private fun updateCategoria(id: Int, categoria: CategoriaP) {
        val call = apiService.updateCategoria(id, categoria)
        call.enqueue(object : Callback<CategoriaP> {
            override fun onResponse(call: Call<CategoriaP>, response: Response<CategoriaP>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CategoriaCRUD, "Categoría modificada correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CategoriaCRUD, MostrarCategoria::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@CategoriaCRUD, "Error al modificar la categoría: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("UpdateCategoria", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CategoriaP>, t: Throwable) {
                Toast.makeText(this@CategoriaCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateCategoria", "Error de conexión", t)
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