package com.example.foodhub_app.ui.feature.add_address

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

@SuppressLint("UnrememberedMutableState")
@Composable
fun AddAddressScreen(
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isPermissionGranted = remember { mutableStateOf(false) }

    RequestPermission(
        onPermissionGranted = {
            isPermissionGranted.value = true
            viewModel.getLocation()
        },
        onPermissionDenied = {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    )

    if (!isPermissionGranted.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val location = viewModel.getLocation().collectAsStateWithLifecycle(initialValue = null)

        Box(modifier = Modifier.fillMaxSize()) {
            location.value?.let { loc ->
                val cameraState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(loc.latitude, loc.longitude), 15f
                    )
                }

                val centerMarker = remember {
                    mutableStateOf(LatLng(loc.latitude, loc.longitude))
                }

                LaunchedEffect(cameraState) {
                    snapshotFlow { cameraState.isMoving }
                        .collectLatest {
                            centerMarker.value = cameraState.position.target
                            if (!cameraState.isMoving) {
                                viewModel.reverseGeocode(
                                    centerMarker.value.latitude,
                                    centerMarker.value.longitude
                                )
                            }
                        }
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true,
                        compassEnabled = true
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = isPermissionGranted.value
                    )
                ) {
                    Marker(
                        state = MarkerState(position = centerMarker.value)
                    )
                }

                val address = viewModel.address.collectAsStateWithLifecycle(initialValue = null)
                address.value?.let {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {}
                            .align(Alignment.BottomCenter),
                        color = Color.White,
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(text = it.addressLine1, style = MaterialTheme.typography.bodyLarge)
                                it.addressLine2?.takeIf { it.isNotEmpty() }?.let { line2 ->
                                    Spacer(Modifier.height(4.dp))
                                    Text(text = line2, style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${it.city}, ${it.state}, ${it.country}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Button(onClick = { viewModel.addAddress() }) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestPermission(onPermissionGranted:()->Unit,onPermissionDenied:()->Unit){
    val context=LocalContext.current
    if(context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
        onPermissionGranted()
        return
    }
    val permission=listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionLauncher= rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {
        if(it.all { it.value }){
            onPermissionGranted()
        }else{
            onPermissionDenied()
        }
    }
    LaunchedEffect(true) {
        permissionLauncher.launch(permission.toTypedArray())
    }
}