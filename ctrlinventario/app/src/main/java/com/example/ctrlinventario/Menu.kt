package com.example.ctrlinventario

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class Menu : ComponentActivity() {
    private lateinit var btnArticulo: Button
    private lateinit var btnCategoria: Button
    private lateinit var btnProveedor: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menuactivity)

        btnArticulo = findViewById(R.id.btnArticulo)
        btnCategoria = findViewById(R.id.btnCategoria)
        btnProveedor = findViewById(R.id.btnProveedor)

        btnArticulo.setOnClickListener {
            val intent = Intent(this@Menu, MostrarArticulo::class.java)
            startActivity(intent)
        }
        btnCategoria.setOnClickListener {
            val intent = Intent(this@Menu, MostrarCategoria::class.java)
            startActivity(intent)
        }
        btnProveedor.setOnClickListener {
            val intent = Intent(this@Menu, MostrarProveedor::class.java)
            startActivity(intent)
        }
    }
}
