package com.example.juntavecinal_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.juntavecinal_grupox.ui.JuntaVecinalApp
import com.example.juntavecinal_grupox.ui.theme.JuntaVecinal_GrupoXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JuntaVecinal_GrupoXTheme {
                JuntaVecinalApp()
            }
        }
    }
}
