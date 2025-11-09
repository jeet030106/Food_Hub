package com.example.foodhub_app.ui.feature.food_item_details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.AddToCart
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
class FoodDetailsViewModel @Inject constructor(private val foodApi: FoodApi): ViewModel() {

    private val _uiState = MutableStateFlow<FoodDetailsUiState>(FoodDetailsUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent=MutableSharedFlow<FoodDetailsEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    private val _quantity=MutableStateFlow<Int>(1)
    val quantity=_quantity.asStateFlow()

    fun incrementQuantity(){
        _quantity.value+=1
        Log.d("increment",quantity.value.toString())
    }
    fun decrementQuantity(){
        if(_quantity.value==1) return
        _quantity.value-=1
        Log.d("increment",quantity.value.toString())
    }

    fun addToCart(restaurantId: String, menuItemId: String?) {
        viewModelScope.launch {
            // 1. Set the UI to a loading state
            _uiState.value = FoodDetailsUiState.Loading

            // 2. Make the safe API call (this is a suspend function call)
            val response = safeApiCall {
                foodApi.addToCart(
                    AddToCart(
                        restaurantId,
                        menuItemId,
                        _quantity.value
                    )
                )
            }

            // 3. Handle the response from the API call
            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = FoodDetailsUiState.Nothing
                    _navigationEvent.emit(FoodDetailsEvent.onAddToCart)
                }

                is ApiResponse.Error -> {
                    _uiState.value = FoodDetailsUiState.Error(response.message)
                    _navigationEvent.emit(FoodDetailsEvent.showErrorDialog(response.message))
                }

                // Note: If ApiResponse is a sealed class with only Success and Error,
                // the 'else' branch may not be necessary.
                else -> {
                    _uiState.value = FoodDetailsUiState.Error("Something went wrong")
                    _navigationEvent.emit(FoodDetailsEvent.showErrorDialog("Something went wrong"))
                }
            }
        }
    }

    fun goToCart(){
        viewModelScope.launch{
            _navigationEvent.emit(FoodDetailsEvent.goToCart)
        }
    }
    sealed class FoodDetailsEvent{
        object goToCart:FoodDetailsEvent()
        data class showErrorDialog(val message: String):FoodDetailsEvent()
        object onAddToCart:FoodDetailsEvent()
    }

    sealed class FoodDetailsUiState{
        object Loading:FoodDetailsUiState()
        data class Error(val message: String):FoodDetailsUiState()
        object Nothing:FoodDetailsUiState()
    }
}