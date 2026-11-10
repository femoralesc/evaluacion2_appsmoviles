package com.example.juntavecinal_grupox.data.model

data class FinancialMovement(
    val id: String,
    val title: String,
    val category: String,
    val amount: Long,
    val date: String,
    val type: MovementType
)

enum class MovementType(val label: String) {
    INGRESO("Ingreso"),
    GASTO("Gasto")
}
