package com.example.foodhub_app.ui.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.CartItem
import com.example.foodhub_app.data.model.CartResponse
import com.example.foodhub_app.data.model.UpdateCartItemRequest
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import com.example.foodhub_app.ui.feature.food_item_details.FoodDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartScreenViewModel@Inject constructor(val foodApi: FoodApi): ViewModel() {

    private val _uiState = MutableStateFlow<cartUiState>(cartUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<cartEvents?>()
    val navigationEvent = _navigationEvent.asSharedFlow()
    private var cartResponse:CartResponse?=null
    init{
        getCart()
    }
    fun getCart(){
        viewModelScope.launch {
            val res= safeApiCall {
                foodApi.getCart()
            }
            when(res){
                is ApiResponse.Success ->{
                    _uiState.value=cartUiState.Success(res.data)
                }
                is ApiResponse.Error ->{
                    _uiState.value=cartUiState.Error(res.message)
                }
                else->{
                    _uiState.value=cartUiState.Error("Something went wrong")
                }
            }
        }
    }

    fun incrementQuantity(cartItem: CartItem){
        updateQuantity(cartItem,cartItem.quantity+1)
    }

    fun decrementQuantity(cartItem: CartItem){
        if(cartItem.quantity==1) return
        updateQuantity(cartItem,cartItem.quantity-1)
    }

    fun updateQuantity(cartItem: CartItem,quantity:Int){
        viewModelScope.launch {
            _uiState.value= cartUiState.Loading
            val res= safeApiCall {
                foodApi.updateCart(UpdateCartItemRequest(cartItem.id, quantity))
            }
            when(res){
                is ApiResponse.Success ->{
                    getCart()
                }
                else->{
                    cartResponse?.let {
                        _uiState.value=cartUiState.Success(cartResponse!!)
                    }
                    _navigationEvent.emit(cartEvents.onQuantityUpdateError)
                }
            }
        }
    }
    fun removeItem(cartItem: CartItem){
        viewModelScope.launch {
            _uiState.value= cartUiState.Loading
            val res= safeApiCall {
                foodApi.removeCartItem(cartItem.id)
            }
            when(res){
                is ApiResponse.Success ->{
                    getCart()
                }
                else->{
                    cartResponse?.let {
                        _uiState.value=cartUiState.Success(cartResponse!!)
                    }
                    _navigationEvent.emit(cartEvents.onItemRemoveError)
                }
            }
        }
    }

    fun checkout(){

    }
    sealed class cartUiState{
        object Loading:cartUiState()
        data class Success(val data: CartResponse):cartUiState()
        data class Error(val message: String):cartUiState()
        object Nothing:cartUiState()
    }

    sealed class cartEvents{
        object OnCheckOut:cartEvents()
        object ShowErrorDialog:cartEvents()
        object onQuantityUpdateError:cartEvents()
        object onItemRemoveError:cartEvents()
    }
}