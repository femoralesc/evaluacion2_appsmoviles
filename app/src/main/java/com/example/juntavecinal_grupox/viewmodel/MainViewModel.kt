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

    fun addReservation(date: String, time: String, purpose: String) {
        repository.addReservation(_uiState.value.activeProfile.demoName, date, time, purpose)
        _uiState.update { it.copy(reservations = repository.reservations) }
    }

    fun updateReservationStatus(id: String, status: ReservationStatus) {
        repository.updateReservationStatus(id, status)
        _uiState.update { it.copy(reservations = repository.reservations) }
    }

    fun addMovement(title: String, category: String, amount: Long, type: MovementType) {
        repository.addMovement(title, category, amount, type)
        _uiState.update { it.copy(movements = repository.movements) }
    }
}
