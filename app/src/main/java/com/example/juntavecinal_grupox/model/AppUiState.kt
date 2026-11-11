package com.example.juntavecinal_grupox.model

data class AppUiState(
    val activeProfile: UserRole = UserRole.VECINO,
    val reservations: List<Reservation> = emptyList(),
    val movements: List<FinancialMovement> = emptyList()
) {
    val totalIncome: Long
        get() = movements.filter { it.type == MovementType.INGRESO }.sumOf { it.amount }

    val totalExpenses: Long
        get() = movements.filter { it.type == MovementType.GASTO }.sumOf { it.amount }

    val balance: Long
        get() = totalIncome - totalExpenses
}
