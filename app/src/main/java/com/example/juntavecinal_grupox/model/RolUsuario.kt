package com.example.juntavecinal_grupox.model

enum class RolUsuario(
    val titulo: String,
    val nombreDemostracion: String,
    val descripcion: String
) {
    VECINO(
        titulo = "Vecino/a",
        nombreDemostracion = "Vecina Demo",
        descripcion = "Solicita el uso de la sede y consulta la transparencia financiera."
    ),
    DIRECTIVA(
        titulo = "Directiva / Secretaría",
        nombreDemostracion = "Equipo Directivo",
        descripcion = "Revisa solicitudes y supervisa los movimientos financieros."
    ),
    TESORERIA(
        titulo = "Tesorería",
        nombreDemostracion = "Tesorería Demo",
        descripcion = "Registra ingresos y gastos y consulta el resumen financiero."
    );

    val tituloFinanciero: String
        get() = if (this == VECINO) "Transparencia" else "Finanzas"

    val puedeVerDetalleFinanciero: Boolean
        get() = this != VECINO

    val puedeRegistrarMovimientos: Boolean
        get() = this == TESORERIA
}
