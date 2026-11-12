package com.example.juntavecinal_grupox.model

enum class RolUsuario(
    val titulo: String,
    val nombreDemostracion: String,
    val descripcion: String
) {
    VECINO(
        titulo = "Vecino/a",
        nombreDemostracion = "Vecina Demo",
        descripcion = "Solicita el uso de la sede y consulta información comunitaria."
    ),
    DIRECTIVA(
        titulo = "Directiva / Secretaría",
        nombreDemostracion = "Equipo Directivo",
        descripcion = "Revisa solicitudes y organiza las reservas de la sede."
    ),
    TESORERIA(
        titulo = "Tesorería",
        nombreDemostracion = "Tesorería Demo",
        descripcion = "Registra ingresos y gastos y consulta el resumen financiero."
    )
}
