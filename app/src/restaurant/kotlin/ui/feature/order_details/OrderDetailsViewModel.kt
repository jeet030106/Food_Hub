package com.example.foodhub_app.ui.feature.order_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.Order
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import com.example.foodhub_app.utils.OrderListUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(private val foodApi: FoodApi): ViewModel(){

    val listOfStatus= OrderListUtils.OrderStatus.entries.map{it.name}
    private val _uiState=MutableStateFlow<States>(States.loading)
    val uiState=_uiState.asStateFlow()

    private val _events= MutableSharedFlow<NavEvents?>()
    val events=_events.asSharedFlow()
    var order: Order? = null
    fun getOrderDetails(orderId: String){
        viewModelScope.launch {
            _uiState.value=States.loading
            val res=safeApiCall {
                foodApi.getOrderDetails(orderId)
            }
            when(res){
                is ApiResponse.Success->{
                    _uiState.value=States.success(res.data)
                    order=res.data
                }
                else->{
                    _uiState.value=States.error
                }
            }
        }
    }

    fun updateOrderStatus(orderId: String,status: String){
        viewModelScope.launch {
            _uiState.value=States.loading
            val res=safeApiCall {
                foodApi.updateOrderStatus(orderId, mapOf("status" to status))
            }
            when(res){
                is ApiResponse.Success->{
                    _events.emit(NavEvents.navigatePopUp("Order Status Updated Successfully"))
                    _uiState.value=States.success(order!!)
                }
                else->{
                    _events.emit(NavEvents.navigatePopUp("Failed to Update Order Status"))
                    _uiState.value=States.error
                }
            }
        }
    }
    sealed class States{
        object loading:States()
        data class success(val order: Order):States()
        object error:States()
    }
    sealed class NavEvents{
        object back: NavEvents()
        data class navigatePopUp(val msg: String): NavEvents()
    }
}