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

class MostrarArticulo : ComponentActivity() {
    private lateinit var listViewArticulo: ListView
    private lateinit var btnIrCrudArticulo: Button
    private lateinit var btnVerCarrito: Button
    private lateinit var btnVolver: Button
    private lateinit var apiService: PApiService
    private val carrito = mutableListOf<ArticuloP>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mostrararticulo)

        listViewArticulo = findViewById(R.id.listViewArticulo)
        btnIrCrudArticulo = findViewById(R.id.btnIrCrudArticulo)
        btnVolver = findViewById(R.id.btnVolver)
        btnVerCarrito = findViewById(R.id.btnVerCarrito)

        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7107/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(PApiService::class.java)

        obtenerTodosLosArticulos()

        // Configurar el botón para ir a la actividad CRUD
        btnIrCrudArticulo.setOnClickListener {
            val intent = Intent(this, ArticuloCRUD::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this@MostrarArticulo, Menu::class.java)
            startActivity(intent)
        }

        btnVerCarrito.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            intent.putParcelableArrayListExtra("carrito", ArrayList(carrito))
            startActivity(intent)
        }
    }

    public fun obtenerTodosLosArticulos() {
        val call = apiService.getAllArticulos()
        call.enqueue(object : Callback<List<ArticuloP>> {
            override fun onResponse(call: Call<List<ArticuloP>>, response: Response<List<ArticuloP>>) {
                if (response.isSuccessful) {
                    val articulos = response.body() ?: emptyList()
                    val articulosFiltrados = articulos.filter { it.estatus == 1 }
                    val adapter = ArticuloAdapter(this@MostrarArticulo, articulosFiltrados, apiService, carrito)
                    listViewArticulo.adapter = adapter
                } else {
                    Toast.makeText(this@MostrarArticulo, "Error al obtener los artículos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ArticuloP>>, t: Throwable) {
                Toast.makeText(this@MostrarArticulo, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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