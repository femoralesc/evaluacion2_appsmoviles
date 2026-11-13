package com.example.juntavecinal_grupox.model

data class Reserva(
    val id: String,
    val nombreSolicitante: String,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val estado: EstadoReserva
)

enum class EstadoReserva(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada")
}

// Cada hora identifica un bloque de una hora en la sede.
val bloquesHorarios = listOf("09:00", "11:00", "13:00", "15:00", "16:00", "18:30")

fun List<Reserva>.estaOcupado(fecha: String, hora: String) =
    any { it.fecha == fecha && it.hora == hora && it.estado == EstadoReserva.APROBADA }
