package com.example.ctrlinventario
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CarritoActivity : ComponentActivity()  {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var carritoAdapter: CarritoAdapter
    private val carrito = mutableListOf<ArticuloP>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        recyclerView = findViewById(R.id.recyclerViewCarrito)
        totalTextView = findViewById(R.id.textViewTotal)

        carrito.addAll(intent.getParcelableArrayListExtra("carrito") ?: emptyList())

        carritoAdapter = CarritoAdapter(this, carrito) { actualizarTotal() }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = carritoAdapter

        actualizarTotal()
    }

    private fun actualizarTotal() {
        val total = carrito.sumByDouble { it.precioUnitario }
        totalTextView.text = "Total: $total"
    }
}