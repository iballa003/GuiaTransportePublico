package org.iesharia.guiatransportepublico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.iesharia.guiatransportepublico.ui.theme.GuiaTransportePublicoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        private lateinit var database: AppDatabase
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuiaTransportePublicoTheme {

        }
    }
}
}

