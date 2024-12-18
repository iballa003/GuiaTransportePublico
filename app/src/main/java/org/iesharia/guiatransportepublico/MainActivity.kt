package org.iesharia.guiatransportepublico

import HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.Modifier
import org.iesharia.guiatransportepublico.data.AppDatabase2
import org.iesharia.guiatransportepublico.ui.StopRepository
import org.iesharia.guiatransportepublico.ui.StopViewModel
import org.iesharia.guiatransportepublico.ui.StopViewModelFactory
import org.iesharia.guiatransportepublico.ui.theme.GuiaTransportePublicoTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase2
    private val stopViewModel: StopViewModel by viewModels {
        StopViewModelFactory(this, StopRepository(AppDatabase2.getDatabase(this).guideDao()))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase2.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            GuiaTransportePublicoTheme {
                HomeScreen(modifier = Modifier,database, stopViewModel)

            }
        }
    }
}

