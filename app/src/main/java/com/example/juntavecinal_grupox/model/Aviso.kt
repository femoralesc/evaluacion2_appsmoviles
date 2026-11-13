package com.example.juntavecinal_grupox.model

data class Aviso(
    val id: String,
    val idDestinatario: String,
    val titulo: String,
    val mensaje: String,
    val fecha: String,
    val leido: Boolean = false
)
