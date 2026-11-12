package com.example.juntavecinal_grupox.model

data class EstadoAplicacion(
    val perfilActivo: RolUsuario = RolUsuario.VECINO,
    val reservas: List<Reserva> = emptyList(),
    val movimientos: List<MovimientoFinanciero> = emptyList()
) {
    val totalIngresos: Long
        get() = movimientos.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto }

    val totalGastos: Long
        get() = movimientos.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }

    val saldo: Long
        get() = totalIngresos - totalGastos
}
