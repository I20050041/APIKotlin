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

class ProveedorCRUD : ComponentActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextRazonSocial: EditText
    private lateinit var editTextDireccion: EditText
    private lateinit var editTextRfc: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var editTextCorreo: EditText
    private lateinit var btnSaveProveedor: Button
    private lateinit var btnUpdateProveedor: Button
    private lateinit var apiService: PApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.proveedorcrud)

        // Inicialización de vistas
        editTextNombre = findViewById(R.id.editTextNombreProveedor)
        editTextRazonSocial = findViewById(R.id.editTextRazonSocial)
        editTextDireccion = findViewById(R.id.editTextDireccion)
        editTextRfc = findViewById(R.id.editTextRfc)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        btnSaveProveedor = findViewById(R.id.btnAddProveedor)
        btnUpdateProveedor = findViewById(R.id.btnUpdateProveedor)

        // Configuración de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7107/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(PApiService::class.java)

        // Recuperar el proveedor seleccionado desde el intent
        val proveedor: ProveedorP? = intent.getParcelableExtra("proveedor")

        // Verificar si se recibió un proveedor válido
        if (proveedor != null) {
            // Mostrar los datos del proveedor en los campos correspondientes
            editTextNombre.setText(proveedor.nombre)
            editTextRazonSocial.setText(proveedor.razonSocial)
            editTextDireccion.setText(proveedor.direccion)
            editTextRfc.setText(proveedor.rfc)
            editTextTelefono.setText(proveedor.telefono)
            editTextCorreo.setText(proveedor.correo)

            // Ocultar el botón de agregar (puede que no sea necesario)
            btnSaveProveedor.visibility = View.GONE

            // Configurar el botón de actualizar
            btnUpdateProveedor.setOnClickListener {
                val nombre = editTextNombre.text.toString()
                val razonSocial = editTextRazonSocial.text.toString()
                val direccion = editTextDireccion.text.toString()
                val rfc = editTextRfc.text.toString()
                val telefono = editTextTelefono.text.toString()
                val correo = editTextCorreo.text.toString()
                val estatus = 1 // Establecer el estatus a 1 por defecto

                val proveedorActualizado = ProveedorP(proveedor.idProveedor, nombre, razonSocial, direccion, rfc, telefono, correo, estatus)
                actualizarProveedor(proveedor.idProveedor, proveedorActualizado)
            }
        } else {
            // Ocultar el botón de actualizar
            btnUpdateProveedor.visibility = View.GONE

            // Configurar el botón de agregar
            btnSaveProveedor.setOnClickListener {
                val nombre = editTextNombre.text.toString()
                val razonSocial = editTextRazonSocial.text.toString()
                val direccion = editTextDireccion.text.toString()
                val rfc = editTextRfc.text.toString()
                val telefono = editTextTelefono.text.toString()
                val correo = editTextCorreo.text.toString()
                val estatus = 1 // Establecer el estatus a 1 por defecto

                val proveedorNuevo = ProveedorP(0, nombre, razonSocial, direccion, rfc, telefono, correo, estatus)
                insertarProveedor(proveedorNuevo)
            }
        }
    }

    private fun insertarProveedor(proveedor: ProveedorP) {
        apiService.addProveedor(proveedor).enqueue(object : Callback<List<ProveedorP>> {
            override fun onResponse(call: Call<List<ProveedorP>>, response: Response<List<ProveedorP>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProveedorCRUD, "Proveedor guardado con éxito", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProveedorCRUD, MostrarProveedor::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ProveedorCRUD, "Error al guardar el proveedor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ProveedorP>>, t: Throwable) {
                Toast.makeText(this@ProveedorCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarProveedor(id: Int, proveedor: ProveedorP) {
        val call = apiService.updateProveedor(id, proveedor)
        call.enqueue(object : Callback<ProveedorP> {
            override fun onResponse(call: Call<ProveedorP>, response: Response<ProveedorP>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProveedorCRUD, "Proveedor actualizado correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProveedorCRUD, MostrarProveedor::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ProveedorCRUD, "Error al actualizar el proveedor: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("ActualizarProveedor", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ProveedorP>, t: Throwable) {
                Toast.makeText(this@ProveedorCRUD, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("ActualizarProveedor", "Error de conexión", t)
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