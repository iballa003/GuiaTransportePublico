import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import kotlinx.coroutines.launch
import org.iesharia.guiatransportepublico.R
import org.iesharia.guiatransportepublico.data.AppDatabase2
import org.iesharia.guiatransportepublico.data.Stop
import org.iesharia.guiatransportepublico.data.StopWithRoute
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
@Composable
fun ScreenManager(modifier: Modifier = Modifier, database: AppDatabase2, viewModel: StopViewModel){
    var screenManager by remember { mutableStateOf("MyMapView") }
    when (screenManager) {
        "MyMapView" -> MyMapView(modifier, database, viewModel, {screenManager="StopListScreen"}, {screenManager="FormAddStop"})
        "StopListScreen" -> StopListScreen(modifier, viewModel, onEdit = { parada ->viewModel.updateStop(parada.id, parada.name, parada.latitude, parada.longitude)},onDelete = { parada ->viewModel.deleteStop(parada.id)}, {screenManager = "MyMapView"})
        else -> FormAddStop(modifier = Modifier, viewModel, {screenManager = "MyMapView"})
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun MyMapView(modifier: Modifier = Modifier, database: AppDatabase2, viewModel: StopViewModel, verLista: () -> Unit, crearForm: () -> Unit) {

    // Obtener el LifecycleOwner dentro del Composable
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val stops by viewModel.allStops.collectAsState()
    
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
                            .size(150.dp)
                            .padding(16.dp)
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
                crearForm()
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
                    verLista()
                         },
            modifier = Modifier.padding(start = 5.dp, top = 80.dp),
            shape = RectangleShape) {
            Text(text = "Ver lista de paradas")
        }
        SimpleToolbar("LanceGuideBus")
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

    val rutas by viewModel.allRoads.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedRutaName by remember { mutableStateOf("") }
    SimpleToolbar("LanceGuideBus")
    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 75.dp),
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
                .padding(top = 55.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleToolbar(
    title: String,
    onNavigationClick: (() -> Unit)? = null, // Acción para el botón de navegación
    navigationIcon: @Composable (() -> Unit)? = null // Ícono de navegación opcional
) {
    // Encabezado
    TopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Start
            )
        },
        navigationIcon = {
            if (navigationIcon != null && onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    navigationIcon()
                }
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

@Composable
fun StopListScreen(
    modifier: Modifier = Modifier,
    viewModel: StopViewModel,
    onEdit: (StopWithRoute) -> Unit,
    onDelete: (StopWithRoute) -> Unit,
    onClose: () -> Unit
) {
    // Observar las paradas con rutas
    val stopsWithRoutes by viewModel.stopsWithRoutes.collectAsState()
    SimpleToolbar("LanceGuideBus")
    Box(modifier = Modifier.fillMaxSize()) {
    IconButton(
        onClick = { onClose() }, // Acción al presionar el botón
        modifier = Modifier.align(Alignment.TopEnd)
            .padding(top = 55.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cerrar",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    Text(text = "Lista de paradas", fontWeight = FontWeight.Bold, fontSize = 25.sp, modifier = Modifier.padding(top = 70.dp))
        if (stopsWithRoutes.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "No hay paradas disponibles", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
        LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 130.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(stopsWithRoutes) { stopWithRoute ->
            StopCard(
                stopWithRoute = stopWithRoute,
                onEdit = { updatedStop ->
                    viewModel.updateStop(updatedStop.id, updatedStop.name, updatedStop.latitude, updatedStop.longitude) },
                onDelete = { onDelete(stopWithRoute) }
            )
        }
    }
    }
    }
}

@Composable
fun StopCard(
    stopWithRoute: StopWithRoute,
    onEdit: (StopWithRoute) -> Unit,
    onDelete: () -> Unit
) {
    var isEditDialogVisible by remember { mutableStateOf(false) } // Estado para mostrar u ocultar el modal
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Información de la Parada
            Column {
                Text(
                    text = stopWithRoute.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Coordenadas: ${stopWithRoute.latitude},${stopWithRoute.longitude}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ruta: ${stopWithRoute.routeName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Botones de Editar y Borrar
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF126e00)), onClick = {isEditDialogVisible = true}, modifier = Modifier.padding(10.dp), shape = RectangleShape) { Text("Editar") }
                    Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFd60e00)), onClick = {onDelete()}, modifier = Modifier.padding(10.dp), shape = RectangleShape) { Text("Borrar") }
                }
            }
        }
    }
    // Modal para Editar la Parada
    if (isEditDialogVisible) {
        EditStopDialog(
            stopWithRoute = stopWithRoute,
            onDismiss = { isEditDialogVisible = false }, // Cerrar el modal
            onConfirm = { updatedStop ->
                isEditDialogVisible = false
                onEdit(updatedStop) // Confirmar los cambios
            }
        )
    }
}

@Composable
fun EditStopDialog(
    stopWithRoute: StopWithRoute,
    onDismiss: () -> Unit, // Acción al cerrar el modal
    onConfirm: (StopWithRoute) -> Unit // Acción al confirmar los cambios
) {
    var updatedName by remember { mutableStateOf(stopWithRoute.name) }
    var updatedLatitude by remember { mutableStateOf(stopWithRoute.latitude.toString()) }
    var updatedLongitude by remember { mutableStateOf(stopWithRoute.longitude.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Parada") },
        text = {
            Column {
                OutlinedTextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text("Nombre de la Parada") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = updatedLatitude,
                    onValueChange = { updatedLatitude = it },
                    label = { Text("Latitud") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = updatedLongitude,
                    onValueChange = { updatedLongitude = it },
                    label = { Text("Longitud") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedStop = stopWithRoute.copy(
                        name = updatedName,
                        latitude = updatedLatitude.toDoubleOrNull() ?: stopWithRoute.latitude,
                        longitude = updatedLongitude.toDoubleOrNull() ?: stopWithRoute.longitude
                    )
                    onConfirm(updatedStop)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

}