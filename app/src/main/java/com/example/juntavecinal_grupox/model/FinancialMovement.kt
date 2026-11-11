package com.example.juntavecinal_grupox.model

data class FinancialMovement(
    val id: String,
    val title: String,
    val category: String,
    val amount: Long,
    val date: String,
    val type: MovementType,
    val reservationId: String? = null
)

enum class MovementType(val label: String) {
    INGRESO("Ingreso"),
    GASTO("Gasto")
}
