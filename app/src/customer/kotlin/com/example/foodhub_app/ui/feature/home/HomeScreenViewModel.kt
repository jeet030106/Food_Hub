package com.example.foodhub_app.ui.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.Category
import com.example.foodhub_app.data.model.Restaurants
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
class HomeScreenViewModel@Inject constructor(private val foodApi: FoodApi): ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState=_uiState.asStateFlow()

    private val _navigationEvent=MutableSharedFlow<HomeScreenNavigationEvents?>()
    val navigationEvent=_navigationEvent.asSharedFlow()
    var categories=emptyList<Category>()
    var restaurants=emptyList<Restaurants>()
    init {
        viewModelScope.launch {
            getCategory()
            getPopularRestaurants()
            if(categories.isNotEmpty() && restaurants.isNotEmpty()){
                _uiState.value=HomeScreenState.Success
            }else{
                _uiState.value=HomeScreenState.Empty
            }
        }
    }

    private suspend fun getCategory(){
            val response= safeApiCall {
                foodApi.getCategories()

            }
        Log.d("JeetC",response.toString())
            when(response){
                is ApiResponse.Success->{
                    categories=response.data.data
                    Log.d("Jeet",categories.toString())
                }

                else -> {
                }
            }

    }

    private suspend fun getPopularRestaurants(){
        val response= safeApiCall {
            foodApi.getRestaurants(40.7128,-74.0060)
        }
        when(response){
            is ApiResponse.Success->{
                restaurants=response.data.data
               Log.d("Jeet",restaurants.toString())
            }
            else -> {
            }
        }
    }

    fun onRestaurantSelected(it: Restaurants){
        viewModelScope.launch {
            _navigationEvent.emit(
                HomeScreenNavigationEvents.NavigateToDetail(
                    it.name,
                    it.imageUrl,
                    it.id
                )
            )
        }
    }
    sealed class HomeScreenState{
        object Empty :HomeScreenState()
        object Loading:HomeScreenState()
        object Success:HomeScreenState()
    }

    sealed class HomeScreenNavigationEvents{
        data class NavigateToDetail(val name:String,val imageUrl:String,val restaurantId:String):HomeScreenNavigationEvents()
    }
}