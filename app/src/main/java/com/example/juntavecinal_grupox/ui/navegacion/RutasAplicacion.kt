package com.example.juntavecinal_grupox.ui.navegacion

sealed class RutasAplicacion (val ruta: String) {

    data object Inicio : RutasAplicacion(ruta = "home_page")

    data object Reservas : RutasAplicacion(ruta = "reservations_page")

    data object Finanzas : RutasAplicacion(ruta = "finance_page")

    data object Perfil : RutasAplicacion(ruta = "profile_page")

    data object FormularioReserva : RutasAplicacion(ruta = "reservation_form_page")

    data object EditarReserva : RutasAplicacion(ruta = "editar_reserva")

    data object FormularioMovimiento : RutasAplicacion(ruta = "movement_form_page")


}
