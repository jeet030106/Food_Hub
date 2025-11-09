package com.example.foodhub_app.ui.feature.add_address

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.location.LocationManager
import com.example.foodhub_app.data.model.Address
import com.example.foodhub_app.data.model.ReverseGeocodeRequest
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(private val locationManager: LocationManager,private val foodApi: FoodApi): ViewModel() {

    private val _state=MutableStateFlow<AddAddressState>(AddAddressState.Loading)
    val state=_state.asStateFlow()

    private val _address=MutableStateFlow<Address?>(null)
    val address=_address.asStateFlow()

    val _events=MutableSharedFlow<AddAddressEvents>()
    val events=_events.asSharedFlow()
    fun reverseGeocode(latitude: Double, longitude: Double){
        viewModelScope.launch {
            _address.value=null
            val res= safeApiCall {
                foodApi.reverseGeocode(ReverseGeocodeRequest(latitude, longitude))
            }
            when(res){
                is ApiResponse.Success->{
                    _address.value=res.data
                    _state.value=AddAddressState.Success
                }
                else->{
                    _state.value=AddAddressState.Error("Failed to Geocode")
                    _address.value=null
                }
            }
        }
    }

    fun addAddress(){
        viewModelScope.launch {
            _events.emit(AddAddressEvents.ShowFinalDialog)
        }
    }
    fun getLocation()=locationManager.getCurrentLocation()
    sealed class AddAddressState{
        object Loading: AddAddressState()
        object Success: AddAddressState()
        data class Error(val message: String): AddAddressState()
    }
    sealed class AddAddressEvents{
        object NavigateToAddressListScreen: AddAddressEvents()
        object ShowFinalDialog: AddAddressEvents()
    }

}