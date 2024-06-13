package com.example.ctrlinventario

data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val estatus: Int
)