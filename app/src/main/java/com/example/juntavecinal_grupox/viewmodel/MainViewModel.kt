package com.example.juntavecinal_grupox.viewmodel

import androidx.lifecycle.ViewModel
import com.example.juntavecinal_grupox.model.AppUiState
import com.example.juntavecinal_grupox.model.MovementType
import com.example.juntavecinal_grupox.model.ReservationStatus
import com.example.juntavecinal_grupox.model.UserRole
import com.example.juntavecinal_grupox.repository.JuntaVecinalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val repository = JuntaVecinalRepository()
    private val _uiState = MutableStateFlow(
        AppUiState(reservations = repository.reservations, movements = repository.movements)
    )
    val uiState = _uiState.asStateFlow()

    fun selectProfile(profile: UserRole) {
        _uiState.update { it.copy(activeProfile = profile) }
    }

    fun addReservation(date: String, time: String, purpose: String): Boolean {
        val added = repository.addReservation(_uiState.value.activeProfile.demoName, date, time, purpose)
        _uiState.update { it.copy(reservations = repository.reservations) }
        return added
    }

    fun updateReservationStatus(id: String, status: ReservationStatus): Boolean {
        val updated = repository.updateReservationStatus(id, status)
        _uiState.update { it.copy(reservations = repository.reservations) }
        return updated
    }

    fun registerPayment(id: String, amount: Long): Boolean {
        if (_uiState.value.activeProfile != UserRole.TESORERIA) return false
        val paid = repository.registerPayment(id, amount)
        _uiState.update { it.copy(movements = repository.movements) }
        return paid
    }

    fun addMovement(title: String, category: String, amount: Long, type: MovementType) {
        repository.addMovement(title, category, amount, type)
        _uiState.update { it.copy(movements = repository.movements) }
    }
}
