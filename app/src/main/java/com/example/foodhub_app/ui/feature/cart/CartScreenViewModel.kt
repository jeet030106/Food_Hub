package com.example.foodhub_app.ui.feature.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.*
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
class CartScreenViewModel @Inject constructor(private val foodApi: FoodApi) : ViewModel() {

    private val _uiState = MutableStateFlow<cartUiState>(cartUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Changed replay=1 → replay=0 so events aren’t replayed later
    private val _navigationEvent = MutableSharedFlow<cartEvents>(replay = 0)
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _itemCount = MutableStateFlow(0)
    val itemCount = _itemCount.asStateFlow()

    private var cartResponse: CartResponse? = null

    private var paymentIntent: PaymentIntentResponse? = null
    private val addressID = "85a9c641-f6bd-4362-8692-e5fb9452c44a"

    init {
        getCart()
    }

    fun getCart() {
        viewModelScope.launch {
            val res = safeApiCall { foodApi.getCart() }
            when (res) {
                is ApiResponse.Success -> {
                    cartResponse = res.data
                    _itemCount.value = res.data.items.size
                    _uiState.value = cartUiState.Success(res.data)
                }
                is ApiResponse.Error -> {
                    _uiState.value = cartUiState.Error(res.message)
                }
                else -> {
                    _uiState.value = cartUiState.Error("Something went wrong")
                }
            }
        }
    }

    fun onAddressSelect() {
        viewModelScope.launch {
            _navigationEvent.emit(cartEvents.onAddressSelect)
        }
    }

    fun incrementQuantity(cartItem: CartItem) {
        updateQuantity(cartItem, cartItem.quantity + 1)
    }

    fun decrementQuantity(cartItem: CartItem) {
        if (cartItem.quantity == 1) return
        updateQuantity(cartItem, cartItem.quantity - 1)
    }

    fun updateQuantity(cartItem: CartItem, quantity: Int) {
        viewModelScope.launch {
            _uiState.value = cartUiState.Loading
            val res = safeApiCall { foodApi.updateCart(UpdateCartItemRequest(cartItem.id, quantity)) }
            when (res) {
                is ApiResponse.Success -> {
                    getCart()
                }
                else -> {
                    cartResponse?.let {
                        _uiState.value = cartUiState.Success(cartResponse!!)
                    }
                    _navigationEvent.emit(cartEvents.onQuantityUpdateError)
                }
            }
        }
    }

    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            _uiState.value = cartUiState.Loading
            val res = safeApiCall { foodApi.removeCartItem(cartItem.id) }
            when (res) {
                is ApiResponse.Success -> {
                    getCart()
                }
                else -> {
                    cartResponse?.let {
                        _uiState.value = cartUiState.Success(cartResponse!!)
                    }
                    _navigationEvent.emit(cartEvents.onItemRemoveError)
                }
            }
        }
    }

    fun checkout() {
        viewModelScope.launch {
            _uiState.value = cartUiState.Loading
            val res = safeApiCall<PaymentIntentResponse> {
                foodApi.getPaymentIntent(PaymentIntentRequest(addressID))
            }
            when (res) {
                is ApiResponse.Success -> {
                    paymentIntent = res.data
                    Log.d("Payment", "checkout -> got PaymentIntentResponse, emitting OnInitiatePayment")
                    _navigationEvent.emit(cartEvents.OnInitiatePayment(res.data))
                    _uiState.value = cartUiState.Success(cartResponse!!)
                }
                is ApiResponse.Error -> {
                    Log.e("Payment", "API call to get payment intent failed. Reason: ${res.message}")
                }
                is ApiResponse.Exception -> {
                    Log.e("Payment", "Exception occurred -> ${res.exception.localizedMessage}", res.exception)
                }
            }
        }
    }

    fun onPaymentSuccess() {
        viewModelScope.launch {
            Log.d("Payment", "onPaymentSuccess() called - verifying purchase with server")
            _uiState.value = cartUiState.Loading
            val res = safeApiCall {
                foodApi.verifyPurchase(
                    ConfirmPaymentRequest(paymentIntent?.paymentIntentId ?: "", addressID),
                    paymentIntent?.paymentIntentId ?: ""
                )
            }
            Log.e("verifyPurchase", "Raw response: ${res}")
            when (res) {
                is ApiResponse.Success -> {
                    Log.d("Payment", "verifyPurchase success -> orderId=${res.data.orderId}")
                    _navigationEvent.emit(cartEvents.OrderSuccess(res.data.orderId))
                    _uiState.value = cartUiState.Success(cartResponse!!)
                    getCart()
                }
                is ApiResponse.Error -> {
                    Log.e("Payment", "verifyPurchase failed: ${res.message}")
                }
                is ApiResponse.Exception -> {
                    Log.e("Payment", "verifyPurchase exception: ${res.exception.localizedMessage}", res.exception)
                }
            }
        }
    }

    fun onPaymentFailed() {
        Log.e("Payment", "onPaymentFailed() called")
        viewModelScope.launch {
            _navigationEvent.emit(cartEvents.ShowErrorDialog)
        }
    }

    sealed class cartUiState {
        object Loading : cartUiState()
        data class Success(val data: CartResponse) : cartUiState()
        data class Error(val message: String) : cartUiState()
        object Nothing : cartUiState()
    }

    sealed class cartEvents {
        object OnCheckOut : cartEvents()
        object ShowErrorDialog : cartEvents()
        object onQuantityUpdateError : cartEvents()
        object onItemRemoveError : cartEvents()
        object onAddressSelect : cartEvents()
        data class OnInitiatePayment(val data: PaymentIntentResponse) : cartEvents()
        data class OrderSuccess(val orderId: String?) : cartEvents()
    }
}
