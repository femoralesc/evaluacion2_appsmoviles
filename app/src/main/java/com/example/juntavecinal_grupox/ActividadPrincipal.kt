package com.example.juntavecinal_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.juntavecinal_grupox.ui.navegacion.NavegacionAplicacion
import com.example.juntavecinal_grupox.ui.tema.TemaJuntaVecinal
import com.example.juntavecinal_grupox.viewmodel.PrincipalViewModel

class ActividadPrincipal : ComponentActivity() {


    private val modeloPrincipal: PrincipalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaJuntaVecinal {

                NavegacionAplicacion(modeloPrincipal = modeloPrincipal)
            }
        }
    }
}
