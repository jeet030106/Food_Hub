package com.example.foodhub_app.ui.feature.address_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.Address
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddressListViewModel@Inject constructor(val foodApi: FoodApi): ViewModel() {

    val _uiState= MutableStateFlow<AddressStates>(AddressStates.Loading)
    val uiState=_uiState.asStateFlow()

    val _navigationEvent=MutableSharedFlow<AddressEvents?>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    init{
        getAddress()
    }

    fun getAddress(){
        viewModelScope.launch{
            val res= safeApiCall {
                foodApi.getAddresses()
            }
            when(res){
                is ApiResponse.Success -> {
                    _uiState.value=AddressStates.Success(res.data.addresses)
                }
                is ApiResponse.Error -> {
                    _uiState.value=AddressStates.Error(res.message)
                }
                else->{
                    _uiState.value=AddressStates.Error("Something went wrong")
                }
            }
        }
    }

    fun onAddAddressClick(){
        viewModelScope.launch{
            _navigationEvent.emit(AddressEvents.NavigateToAddAddress)
        }
    }

    sealed class AddressStates{
        object Loading:AddressStates()
        data class Success(val addresses:List<Address>):AddressStates()
        data class Error(val message:String):AddressStates()
    }

    sealed class AddressEvents{
        data class NavigateToEditAddress(val address:Address):AddressEvents()
        object NavigateToAddAddress:AddressEvents()
    }
}