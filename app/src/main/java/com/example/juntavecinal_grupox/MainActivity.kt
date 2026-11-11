package com.example.juntavecinal_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.juntavecinal_grupox.ui.navigation.AppNavigation
import com.example.juntavecinal_grupox.ui.theme.JuntaVecinal_GrupoXTheme
import com.example.juntavecinal_grupox.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {


    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JuntaVecinal_GrupoXTheme {

                AppNavigation(viewModel = mainViewModel)
            }
        }
    }
}
