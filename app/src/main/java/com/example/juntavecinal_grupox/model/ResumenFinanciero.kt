package com.example.juntavecinal_grupox.model

data class ResumenFinanciero(
    val titulo: String,
    val nombreMes: String,
    val totalIngresos: Long,
    val totalGastos: Long,
    val ingresosPorCategoria: Map<String, Long>,
    val gastosPorCategoria: Map<String, Long>,
    val movimientos: List<MovimientoFinanciero>,
    val puedeVerDetalle: Boolean,
    val puedeRegistrar: Boolean
) {
    val saldo: Long
        get() = totalIngresos - totalGastos
}
