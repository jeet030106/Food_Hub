package com.example.foodhub_app.ui.feature.menu.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.FoodHubSession
import com.example.foodhub_app.data.model.FoodItem
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import com.google.android.gms.common.api.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListMenuViewModel @Inject constructor(private val foodApi: FoodApi,private val session: FoodHubSession): ViewModel() {


    private val _uiStates=MutableStateFlow<states>(states.loading)
    val uiStates=_uiStates.asStateFlow()

    private val _events=MutableSharedFlow<Events>()
    val events=_events.asSharedFlow()

    fun onAddItem(){
        viewModelScope.launch {
            _events.emit(Events.addItem)
        }
    }

    init{
        getListItem()
    }
    fun getListItem(){
        viewModelScope.launch {
            _uiStates.value= states.loading
            val restaurantId=session.getRestaurantId()
            val res= safeApiCall { foodApi.getRestaurantMenu(restaurantId!!) }
            when(res){
                is ApiResponse.Success->{
                    _uiStates.value= states.success(res.data.foodItems)
                }
                is ApiResponse.Error->{
                    _uiStates.value= states.error(res.message)
                }
                else -> {
                    _uiStates.value= states.error("Something went wrong")
                }
            }
        }
    }

    fun retry(){
        viewModelScope.launch {
            _uiStates.value= states.loading
            getListItem()
        }
    }
    sealed class Events{
        object addItem:Events()
    }
    sealed class states{
        object loading:states()
        data class success(val data:List<FoodItem>):states()
        data class error(val msg:String):states()
    }
}