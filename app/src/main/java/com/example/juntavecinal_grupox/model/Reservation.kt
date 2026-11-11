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

// Cada hora identifica un bloque de una hora en la sede.
val bookingSlots = listOf("09:00", "11:00", "13:00", "15:00", "16:00", "18:30")

fun List<Reservation>.isBooked(date: String, time: String) =
    any { it.date == date && it.time == time && it.status == ReservationStatus.APROBADA }
