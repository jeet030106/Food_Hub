package com.example.foodhub_app.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.FoodHubSession
import com.example.foodhub_app.data.model.Restaurants
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import com.stripe.android.core.injection.InjectorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val foodApi: FoodApi,val session: FoodHubSession): ViewModel() {

    private val _uiState= MutableStateFlow<states>(states.loading)
    val uiState=_uiState.asStateFlow()

    init{
        getProfile()
    }
    fun getProfile(){
        _uiState.value= states.loading
        viewModelScope.launch{
            val res= safeApiCall {
                foodApi.getRestaurantProfile()
            }

            when(res){
                is ApiResponse.Success->{
                    _uiState.value=states.success(res.data)
                    session.storeRestaurantId(res.data.id)
                }

                else->{
                    _uiState.value=states.error
                }
            }
        }
    }
    sealed class states{
        object loading:states()
        object error:states()
        data class success(val data: Restaurants):states()
    }
}