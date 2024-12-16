package org.iesharia.guiatransportepublico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.iesharia.guiatransportepublico.data.AppDatabase2
import org.iesharia.guiatransportepublico.ui.theme.GuiaTransportePublicoTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase2.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            GuiaTransportePublicoTheme {

        }
    }
}
}

