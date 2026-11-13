package com.example.juntavecinal_grupox.model

data class EstadoAplicacion(
    val usuarioActual: Usuario? = null,
    val errorAcceso: String? = null,
    val reservas: List<Reserva> = emptyList(),
    val movimientos: List<MovimientoFinanciero> = emptyList(),
    val desplazamientoMes: Int = 0,
    val idReservaEnEdicion: String? = null
) {
    val perfilActivo: RolUsuario
        get() = usuarioActual?.rol ?: RolUsuario.VECINO

    val totalIngresos: Long
        get() = movimientos.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto }

    val totalGastos: Long
        get() = movimientos.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }

    val saldo: Long
        get() = totalIngresos - totalGastos
}
