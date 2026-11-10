package com.example.juntavecinal_grupox.data.model

enum class UserRole(
    val title: String,
    val demoName: String,
    val description: String
) {
    VECINO(
        title = "Vecino/a",
        demoName = "Vecina Demo",
        description = "Solicita el uso de la sede y consulta información comunitaria."
    ),
    DIRECTIVA(
        title = "Directiva / Secretaría",
        demoName = "Equipo Directivo",
        description = "Revisa solicitudes y organiza las reservas de la sede."
    ),
    TESORERIA(
        title = "Tesorería",
        demoName = "Tesorería Demo",
        description = "Registra ingresos y gastos y consulta el resumen financiero."
    )
}
