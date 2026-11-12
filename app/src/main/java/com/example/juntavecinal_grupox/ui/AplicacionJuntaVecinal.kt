package com.example.juntavecinal_grupox.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juntavecinal_grupox.ui.navegacion.NavegacionAplicacion
import com.example.juntavecinal_grupox.viewmodel.PrincipalViewModel

@Composable
fun AplicacionJuntaVecinal(modeloPrincipal: PrincipalViewModel = viewModel()) {
    NavegacionAplicacion(modeloPrincipal)
}
