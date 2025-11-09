package com.example.foodhub_app.ui.feature.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.Order
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
class OrderListViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {

    private val _uiState=MutableStateFlow<OrderState>(OrderState.Loading)
    val uiState=_uiState.asStateFlow()

    private val _events=MutableSharedFlow<OrderEvents>()
    val events=_events.asSharedFlow()


    init{
        getOrders()
    }
    fun getOrders(){
        viewModelScope.launch {
            _uiState.value=OrderState.Loading
            val res= safeApiCall { foodApi.getOrders() }
            when(res){
                is ApiResponse.Success->{
                    _uiState.value=OrderState.OrderList(res.data.orders)
                }
                is ApiResponse.Error->{
                    _uiState.value=OrderState.Error(res.message)
                }
                else->{
                    _uiState.value=OrderState.Error("Something went wrong")
                }
            }
        }
    }

    fun navigateToDetail(order:Order){
        viewModelScope.launch {
            _events.emit(OrderEvents.NavigateToDetail(order))
        }
    }
    fun navigateToBack(){
        viewModelScope.launch {
            _events.emit(OrderEvents.NavigateToBack)
        }
    }
    sealed class OrderState{
        object Loading:OrderState()
        data class OrderList(val orderList:List<Order>):OrderState()
        data class Error(val message:String):OrderState()
    }

    sealed class OrderEvents{
        data class NavigateToDetail(val order:Order):OrderEvents()
        object NavigateToBack:OrderEvents()
    }
}