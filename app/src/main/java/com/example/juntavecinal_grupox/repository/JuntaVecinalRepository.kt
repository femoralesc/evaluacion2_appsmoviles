package com.example.juntavecinal_grupox.repository

import com.example.juntavecinal_grupox.model.FinancialMovement
import com.example.juntavecinal_grupox.model.MovementType
import com.example.juntavecinal_grupox.model.Reservation
import com.example.juntavecinal_grupox.model.ReservationStatus
import com.example.juntavecinal_grupox.model.UserRole
import com.example.juntavecinal_grupox.model.isBooked
import com.example.juntavecinal_grupox.model.bookingSlots
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class JuntaVecinalRepository {
    var reservations = demoReservations()
        private set

    var movements = demoMovements()
        private set

    fun addReservation(requesterName: String, date: String, time: String, purpose: String): Boolean {
        if (time !in bookingSlots || purpose.isBlank() || reservations.isBooked(date, time)) return false
        val reservation = Reservation(
            id = UUID.randomUUID().toString(),
            requesterName = requesterName,
            date = date,
            time = time,
            purpose = purpose,
            status = ReservationStatus.PENDIENTE
        )

        reservations = listOf(reservation) + reservations
        return true
    }

    fun updateReservationStatus(id: String, status: ReservationStatus): Boolean {
        val request = reservations.find { it.id == id && it.status == ReservationStatus.PENDIENTE } ?: return false
        if (status == ReservationStatus.APROBADA && reservations.isBooked(request.date, request.time)) return false
        reservations = reservations.map { reservation ->
            if (reservation.id == id) reservation.copy(status = status) else reservation
        }
        return true
    }

    fun registerPayment(id: String, amount: Long): Boolean {
        val reservation = reservations.find { it.id == id && it.status == ReservationStatus.APROBADA } ?: return false
        if (amount <= 0 || movements.any { it.reservationId == id }) return false
        addMovement("Arriendo · " + reservation.purpose, "Pago de arriendo", amount, MovementType.INGRESO, id)
        return true
    }

    fun addMovement(title: String, category: String, amount: Long, type: MovementType, reservationId: String? = null) {
        if (amount <= 0) return

        val movement = FinancialMovement(
            id = UUID.randomUUID().toString(),
            title = title,
            category = category,
            amount = amount,
            date = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-CL")).format(Date()),
            type = type,
            reservationId = reservationId
        )

        movements = listOf(movement) + movements
    }
}

private fun demoReservations() = listOf(
    Reservation(
        id = "R-001",
        requesterName = UserRole.VECINO.demoName,
        date = "14/11/2026",
        time = "16:00",
        purpose = "Taller comunitario",
        status = ReservationStatus.APROBADA
    ),
    Reservation(
        id = "R-002",
        requesterName = "Vecino Demo 2",
        date = "18/11/2026",
        time = "18:30",
        purpose = "Reunión vecinal",
        status = ReservationStatus.PENDIENTE
    ),
    Reservation(
        id = "R-003",
        requesterName = "Vecina Demo 3",
        date = "21/11/2026",
        time = "15:00",
        purpose = "Actividad familiar",
        status = ReservationStatus.PENDIENTE
    )
)

private fun demoMovements() = listOf(
    FinancialMovement(
        id = "M-001",
        title = "Arriendo de sede",
        category = "Ingreso por arriendo",
        amount = 35000,
        date = "04/11/2026",
        type = MovementType.INGRESO
    ),
    FinancialMovement(
        id = "M-002",
        title = "Consumo eléctrico",
        category = "Servicios básicos",
        amount = 18500,
        date = "03/11/2026",
        type = MovementType.GASTO
    ),
    FinancialMovement(
        id = "M-003",
        title = "Arriendo de sede",
        category = "Ingreso por arriendo",
        amount = 30000,
        date = "31/10/2026",
        type = MovementType.INGRESO
    )
)
