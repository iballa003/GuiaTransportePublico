import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.iesharia.guiatransportepublico.R
import org.iesharia.guiatransportepublico.data.AppDatabase2
import org.iesharia.guiatransportepublico.data.Road
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
    var boolTest by remember { mutableStateOf(true) }
    if(boolTest){
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
            stops.forEach { parada ->
                Marker(
                    state = rememberMarkerState(
                        geoPoint = GeoPoint(parada.latitude, parada.longitude)
                    ),
                    title = parada.name,
                    snippet = "",
                    icon = context.getDrawable(R.drawable.bus)
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
        }
        FloatingActionButton(
            onClick = {
                boolTest = false
                      },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Centrar mapa"
            )
        }
        Button(onClick = {

                         }, modifier = Modifier.padding(start = 5.dp, top = 10.dp)) { Text(text = "Ver lista de paradas") }
    }
    }
    else{
        FormAddStop(modifier = Modifier, viewModel, {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormAddStop(modifier: Modifier = Modifier, viewModel: StopViewModel, onClose: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    // Estados locales para los campos del formulario
    var nombreParada by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }
    var selectedRoadId by remember { mutableStateOf<Int?>(null) }
    var mensajeExito by remember { mutableStateOf("") }

    val rutas by viewModel.rutas.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedRutaName by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Agregar Nueva Parada", modifier = Modifier.padding(8.dp))

        // Campo: Nombre de la Parada
        OutlinedTextField(
            value = nombreParada,
            onValueChange = { nombreParada = it },
            label = { Text("Nombre de la Parada") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo: Latitud
        OutlinedTextField(
            value = latitud,
            onValueChange = { latitud = it },
            label = { Text("Latitud") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo: Longitud
        OutlinedTextField(
            value = longitud,
            onValueChange = { longitud = it },
            label = { Text("Longitud") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Dropdown para seleccionar la Ruta
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedRutaName,
                onValueChange = {},
                label = { Text("Seleccionar Ruta") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                rutas.forEach { ruta ->
                    DropdownMenuItem(
                        text = { Text(ruta.name) },
                        onClick = {
                            selectedRutaName = ruta.name
                            selectedRoadId = ruta.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Guardar
        Button(
            onClick = {
                // Validar entrada y guardar en la base de datos
                if (nombreParada.isNotEmpty() && latitud.isNotEmpty() && longitud.isNotEmpty()) {
                    coroutineScope.launch {
                        val parada = Stop(
                            name = nombreParada,
                            latitude = latitud.toDouble(),
                            longitude = longitud.toDouble(),
                            road_id = 1 // ID de ruta por defecto, ajusta según lógica
                        )
                        viewModel.insertData(parada)
                        mensajeExito = "Parada guardada exitosamente."
                        // Limpiar campos después de guardar
                        nombreParada = ""
                        latitud = ""
                        longitud = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar mensaje de éxito
        if (mensajeExito.isNotEmpty()) {
            Text(text = mensajeExito, color = androidx.compose.ui.graphics.Color.Green)
        }

    }
        IconButton(
            onClick = { onClose() }, // Acción al presionar el botón
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}