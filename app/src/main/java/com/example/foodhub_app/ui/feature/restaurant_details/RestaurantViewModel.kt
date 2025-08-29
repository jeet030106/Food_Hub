package com.example.foodhub_app.ui.feature.restaurant_details

import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.FoodItem
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.internal.aggregatedroot.codegen._com_example_foodhub_app_FoodHubApp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {
    var errorMsg="";
    var errorDesc="";
    val _uiState = MutableStateFlow<RestaurantEvent>( RestaurantEvent.Nothing )
    val uiState = _uiState.asStateFlow()

    val _navigationEvent = MutableSharedFlow<RestaurantNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun getFoodItems(id: String){
        viewModelScope.launch {
            try{
                _uiState.value = RestaurantEvent.Loading
                val response= safeApiCall {
                    foodApi.getRestaurantFoodItems(id)
                }
                when(response){
                    is ApiResponse.Success -> {
                        _uiState.value = RestaurantEvent.Success(response.data.foodItems)
                    }
                    else->{
                        val error =(response as? ApiResponse.Error)?.code
                        when(error){
                            401->{
                                errorMsg="Unauthorized"
                                errorDesc="You are unauthorized to view this content"
                            }
                            404->{
                                errorMsg="Not Found"
                                errorDesc="The restaurant was not found"
                            }
                            else->{
                                errorMsg="Error"
                                errorDesc="Something went wrong"
                            }
                        }
                    }
                }
            }catch (e:Exception){
                _uiState.value = RestaurantEvent.Error
            }

        }
    }
    sealed class RestaurantNavigationEvent{
        data object GoBack:RestaurantNavigationEvent()
        data object ShowErrorDialog:RestaurantNavigationEvent()
        data class NavigateToHome(val productID:String):RestaurantNavigationEvent()
    }

    sealed class RestaurantEvent{
        data object Nothing:RestaurantEvent()
        data class Success(val foodItem:List<FoodItem>):RestaurantEvent()
        data object Loading:RestaurantEvent()
        data object Error:RestaurantEvent()
    }
}