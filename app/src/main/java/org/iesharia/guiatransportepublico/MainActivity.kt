package org.iesharia.guiatransportepublico

import MyMapView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import org.iesharia.guiatransportepublico.data.AppDatabase2
import org.iesharia.guiatransportepublico.ui.StopRepository
import org.iesharia.guiatransportepublico.ui.StopViewModel
import org.iesharia.guiatransportepublico.ui.StopViewModelFactory
import org.iesharia.guiatransportepublico.ui.theme.GuiaTransportePublicoTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase2
    private val stopViewModel: StopViewModel by viewModels {
        StopViewModelFactory(StopRepository(AppDatabase2.getDatabase(this).guideDao()))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase2.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            GuiaTransportePublicoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyMapView(
                        modifier = Modifier.padding(innerPadding),
                        database,
                        stopViewModel)
                }
            }
        }
    }
}

