package com.example.foodhub_app.ui.feature.order_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.Order
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import com.example.foodhub_app.utils.OrderListUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(private val foodApi: FoodApi) : ViewModel() {

    private val _uiStates=MutableStateFlow<OrderListViewModel.states>(OrderListViewModel.states.loading)
    val uiStates=_uiStates.asStateFlow()
    fun getOrderTypes(): List<String>{
        val types= OrderListUtils.OrderStatus.entries.map{it.name}
        return types
    }

    fun getOrderListByTypes(type:String){
        viewModelScope.launch{
            _uiStates.value=states.loading
            val res=safeApiCall {
                foodApi.getOrderTypes(type)
            }
            when(res){
                is ApiResponse.Success->{
                    _uiStates.value=states.success(res.data.orders)
                }
                else->{
                    _uiStates.value=states.error
                }
            }
        }
    }
    sealed class states{
        object loading:states()
        data class success(val data:List<Order>):states()
        object error:states()
    }
}