package com.example.juntavecinal_grupox.ui.navegacion


sealed class EventoNavegacion {


        data class IrA(

            val ruta: RutasAplicacion,
            val rutaHasta: RutasAplicacion? = null,
            val incluirDestino: Boolean = false,
            val destinoUnico: Boolean = true

        ) : EventoNavegacion()


        object Volver : EventoNavegacion()

        object Subir : EventoNavegacion()


}


