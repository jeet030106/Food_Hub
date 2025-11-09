package com.example.foodhub_app.ui.feature.order_details

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
class OrderDetailViewModel @Inject constructor(private val foodApi: FoodApi) : ViewModel() {

    private val _orderDetailStates=MutableStateFlow<OrderDetailStates>(OrderDetailStates.Loading)
    val orderDetailStates=_orderDetailStates.asStateFlow()

    private val _orderDetailNav= MutableSharedFlow<OrderDetailNav?>()
    val orderDetailNav=_orderDetailNav.asSharedFlow()

    fun getOrderDetail(orderId: String){
        viewModelScope.launch {
            _orderDetailStates.value= OrderDetailStates.Loading
            val res=safeApiCall { foodApi.getOrderDetails(orderId) }
            when(res){
                is ApiResponse.Success->{
                    _orderDetailStates.value=OrderDetailStates.Success(res.data)
                }
                is ApiResponse.Error->{
                    _orderDetailStates.value= OrderDetailStates.Error
                }
                else->{
                    _orderDetailStates.value= OrderDetailStates.Error
                }
            }
        }
    }
    fun onBackClick(){
        viewModelScope.launch {
            _orderDetailNav.emit(OrderDetailNav.Back)
        }
    }
    sealed class OrderDetailStates{
        object Loading:OrderDetailStates()
        data class Success(val order: Order):OrderDetailStates()
        object Error: OrderDetailStates()
    }

    sealed class OrderDetailNav{
        object Back:OrderDetailNav()
    }
}