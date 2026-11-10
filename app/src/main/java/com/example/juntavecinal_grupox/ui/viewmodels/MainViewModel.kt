package com.example.juntavecinal_grupox.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.juntavecinal_grupox.data.model.FinancialMovement
import com.example.juntavecinal_grupox.data.model.MovementType
import com.example.juntavecinal_grupox.data.model.Reservation
import com.example.juntavecinal_grupox.data.model.ReservationStatus
import com.example.juntavecinal_grupox.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class AppUiState(
    val activeProfile: UserRole = UserRole.VECINO,
    val reservations: List<Reservation> = demoReservations(),
    val movements: List<FinancialMovement> = demoMovements()
) {
    val totalIncome: Long
        get() = movements.filter { it.type == MovementType.INGRESO }.sumOf { it.amount }

    val totalExpenses: Long
        get() = movements.filter { it.type == MovementType.GASTO }.sumOf { it.amount }

    val balance: Long
        get() = totalIncome - totalExpenses
}

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState = _uiState.asStateFlow()

    fun selectProfile(profile: UserRole) {
        _uiState.update { currentState -> currentState.copy(activeProfile = profile) }
    }

    fun addReservation(date: String, time: String, purpose: String) {
        val reservation = Reservation(
            id = UUID.randomUUID().toString(),
            requesterName = _uiState.value.activeProfile.demoName,
            date = date,
            time = time,
            purpose = purpose,
            status = ReservationStatus.PENDIENTE
        )

        _uiState.update { currentState ->
            currentState.copy(reservations = listOf(reservation) + currentState.reservations)
        }
    }

    fun updateReservationStatus(id: String, status: ReservationStatus) {
        _uiState.update { currentState ->
            currentState.copy(
                reservations = currentState.reservations.map { reservation ->
                    if (reservation.id == id) reservation.copy(status = status) else reservation
                }
            )
        }
    }

    fun addMovement(title: String, category: String, amount: Long, type: MovementType) {
        if (amount <= 0) return

        val movement = FinancialMovement(
            id = UUID.randomUUID().toString(),
            title = title,
            category = category,
            amount = amount,
            date = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-CL")).format(Date()),
            type = type
        )

        _uiState.update { currentState ->
            currentState.copy(movements = listOf(movement) + currentState.movements)
        }
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
        date = "01/11/2026",
        type = MovementType.INGRESO
    )
)
