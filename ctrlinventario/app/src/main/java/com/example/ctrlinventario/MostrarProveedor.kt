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

class MostrarProveedor : ComponentActivity() {
    private lateinit var listViewProveedor: ListView
    private lateinit var btnIrCrud: Button
    private lateinit var apiService: PApiService
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mostrarproveedor)

        listViewProveedor = findViewById(R.id.listViewProveedor)
        btnIrCrud = findViewById(R.id.btnIrCrud)
        btnVolver = findViewById(R.id.btnvolver)

        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7107/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(PApiService::class.java)

        obtenerTodosLosProveedores()

        // Configurar el botón para ir a la actividad CRUD
        btnIrCrud.setOnClickListener {
            val intent = Intent(this, ProveedorCRUD::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this@MostrarProveedor, Menu::class.java)
            startActivity(intent)
        }
    }

    public fun obtenerTodosLosProveedores() {
        val call = apiService.getAllProveedor()
        call.enqueue(object : Callback<List<ProveedorP>> {
            override fun onResponse(call: Call<List<ProveedorP>>, response: Response<List<ProveedorP>>) {
                if (response.isSuccessful) {
                    val proveedores = response.body() ?: emptyList()
                    val proveedoresFiltrados = proveedores.filter { it.estatus == 1 }
                    val adapter = ProveedorAdapter(this@MostrarProveedor, proveedoresFiltrados, apiService)
                    listViewProveedor.adapter = adapter
                } else {
                    Toast.makeText(this@MostrarProveedor, "Error al obtener los proveedores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ProveedorP>>, t: Throwable) {
                Toast.makeText(this@MostrarProveedor, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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