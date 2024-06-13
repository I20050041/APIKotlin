package com.example.ctrlinventario

import PApiService
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
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

    public class MostrarCategoria : ComponentActivity() {
    private lateinit var listViewCategoria: ListView
    private lateinit var btnIrCrud: Button
    private lateinit var apiService: PApiService
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mostrarcategoria)

        listViewCategoria = findViewById(R.id.listViewCategoria)
        btnIrCrud = findViewById(R.id.btnIrCrud)
        btnVolver = findViewById(R.id.btnvolver)

        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7107/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(PApiService::class.java)

        obtenerTodosLasCategorias()

        // Configurar el botón para ir a la actividad CRUD
        btnIrCrud.setOnClickListener {
            val intent = Intent(this, CategoriaCRUD::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this@MostrarCategoria, Menu::class.java)
            startActivity(intent)
        }
    }

    public fun obtenerTodosLasCategorias() {
        val call = apiService.getAllCategorias()
        call.enqueue(object : Callback<List<CategoriaP>> {
            override fun onResponse(call: Call<List<CategoriaP>>, response: Response<List<CategoriaP>>) {
                if (response.isSuccessful) {
                    val categorias = response.body() ?: emptyList()
                    val categoriasFiltradas = categorias.filter { it.estatus == 1 }
                    val adapter = CategoriaAdapter(this@MostrarCategoria, categoriasFiltradas, apiService)
                    listViewCategoria.adapter = adapter
                } else {
                    Toast.makeText(this@MostrarCategoria, "Error al obtener las categorías", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CategoriaP>>, t: Throwable) {
                Toast.makeText(this@MostrarCategoria, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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