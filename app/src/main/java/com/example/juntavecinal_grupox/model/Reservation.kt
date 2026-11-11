package com.example.juntavecinal_grupox.model

data class Reservation(
    val id: String,
    val requesterName: String,
    val date: String,
    val time: String,
    val purpose: String,
    val status: ReservationStatus
)

enum class ReservationStatus(val label: String) {
    PENDIENTE("Pendiente"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada")
}
