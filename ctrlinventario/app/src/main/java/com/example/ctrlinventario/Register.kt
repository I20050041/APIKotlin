package com.example.ctrlinventario
import PApiService
import android.content.Intent
import android.os.Bundle
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

class Register : ComponentActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextCorreo: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var editTextConfirmarContrasena: EditText
    private lateinit var btnRegistrarse: Button
    private lateinit var apiService: PApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        editTextContrasena = findViewById(R.id.editTextContrasena)
        editTextConfirmarContrasena = findViewById(R.id.editTextConfirmarContrasena)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7107/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(PApiService::class.java)

        btnRegistrarse.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val correo = editTextCorreo.text.toString()
            val contrasena = editTextContrasena.text.toString()
            val confirmarContrasena = editTextConfirmarContrasena.text.toString()

            if (contrasena != confirmarContrasena) {
                Toast.makeText(this@Register, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!correo.contains("@")) {
                Toast.makeText(this@Register, "El correo debe contener un dominio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar si el correo ya está registrado
            apiService.getAllUsuarios().enqueue(object : Callback<List<Usuario>> {
                override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                    if (response.isSuccessful) {
                        val usuarios = response.body()
                        if (usuarios != null) {
                            for (usuario in usuarios) {
                                if (usuario.correo == correo) {
                                    Toast.makeText(this@Register, "El correo ya está registrado, por favor elige otro", Toast.LENGTH_SHORT).show()
                                    return
                                }
                            }
                        }
                        // Si el correo no está registrado, proceder con el registro
                        val usuario = Usuario(0, nombre, correo, contrasena, 1) // Suponiendo estatus activo

                        apiService.addUsuario(usuario).enqueue(object : Callback<List<Usuario>> {
                            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@Register, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@Register, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@Register, "Error en el registro", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                                Toast.makeText(this@Register, "Error de conexión", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Toast.makeText(this@Register, "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                    Toast.makeText(this@Register, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })

        }
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