package com.example.juntavecinal_grupox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntavecinal_grupox.model.AppUiState
import com.example.juntavecinal_grupox.model.MovementType
import com.example.juntavecinal_grupox.model.ReservationStatus
import com.example.juntavecinal_grupox.model.UserRole
import com.example.juntavecinal_grupox.repository.JuntaVecinalRepository
import com.example.juntavecinal_grupox.ui.navigation.AppRoutes
import com.example.juntavecinal_grupox.ui.navigation.NavigationEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = JuntaVecinalRepository()
    private val _uiState = MutableStateFlow(
        AppUiState(reservations = repository.reservations, movements = repository.movements)
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun navigateTo(route: AppRoutes, popUpTo: AppRoutes? = null, inclusive: Boolean = false) {
        viewModelScope.launch {
            _navigationEvent.send(
                NavigationEvent.NavigateTo(
                    route = route,
                    popUptoRoute = popUpTo,
                    inclusive = inclusive
                )
            )
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvent.send(NavigationEvent.PopBackStack)
        }
    }

    fun selectProfile(profile: UserRole) {
        _uiState.update { it.copy(activeProfile = profile) }
    }

    fun addReservation(date: String, time: String, purpose: String): Boolean {
        val added = repository.addReservation(_uiState.value.activeProfile.demoName, date, time, purpose)
        if (added) {
            _uiState.update { it.copy(reservations = repository.reservations) }
            navigateBack()
        }
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
        navigateBack()
    }
}