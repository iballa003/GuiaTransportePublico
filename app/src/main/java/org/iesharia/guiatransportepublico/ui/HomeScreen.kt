import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.iesharia.guiatransportepublico.data.AppDatabase2
import org.iesharia.guiatransportepublico.data.Stop
import org.iesharia.guiatransportepublico.ui.StopViewModel
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex

val GoogleSat: OnlineTileSourceBase = object : XYTileSource(
    "Google-Sat",
    0, 19, 256, ".png", arrayOf<String>(
        "https://mt0.google.com",
        "https://mt1.google.com",
        "https://mt2.google.com",
        "https://mt3.google.com",

        )
) {
    override fun getTileURLString(pTileIndex: Long): String {
        return baseUrl + "/vt/lyrs=s&x=" + MapTileIndex.getX(pTileIndex) + "&y=" + MapTileIndex.getY(
            pTileIndex
        ) + "&z=" + MapTileIndex.getZoom(pTileIndex)
    }
}
@SuppressLint("DiscouragedApi")
@Composable
fun MyMapView(modifier: Modifier = Modifier, database: AppDatabase2, viewModel: StopViewModel) {

    // Obtener el LifecycleOwner dentro del Composable
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var stops by remember { mutableStateOf(emptyList<Stop>()) }

    viewModel.allStops.observe(lifecycleOwner) { stopsList ->
        stops = stopsList
    }

    // define camera state
    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(28.957473, -13.554514)
        zoom = 17.0 // optional, default is 5.0
    }

    // define properties with remember with default value
    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    // setup mapProperties in side effect
    SideEffect {
        mapProperties = mapProperties
            .copy(tileSources = GoogleSat)
            .copy(isEnableRotationGesture = true)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            properties = mapProperties // add properties
        ){
                Marker(
                    state = rememberMarkerState(
                        geoPoint = GeoPoint(28.957473, -13.554514)
                    ),
                    title = "Marcador",
                    snippet = "",
                ) {
                    Column(
                        modifier = Modifier
                            .size(100.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                    ) {
                        Text(text = it.title)
                        Text(text = it.snippet, fontSize = 10.sp)
                    }
                }
        }
        FloatingActionButton(
            onClick = { Log.d("MapView", "FAB presionado") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Centrar mapa"
            )
        }
    }
}

