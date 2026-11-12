package com.example.juntavecinal_grupox.model

data class MovimientoFinanciero(
    val id: String,
    val titulo: String,
    val categoria: String,
    val monto: Long,
    val fecha: String,
    val tipo: TipoMovimiento,
    val idReserva: String? = null
)

enum class TipoMovimiento(val etiqueta: String) {
    INGRESO("Ingreso"),
    GASTO("Gasto")
}
