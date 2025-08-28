package com.example.foodhub_app.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.Category
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
    init {
        getCategory()
        getPopularRestaurants()
    }

    fun getCategory(){
        viewModelScope.launch {
            val response= safeApiCall {
                foodApi.getCategories()
            }
            when(response){
                is ApiResponse.Success->{
                    categories=response.data.data
                    _uiState.value=HomeScreenState.Success
                }
                is ApiResponse.Error->{
                    _uiState.value=HomeScreenState.Empty
                }
                else -> {
                    _uiState.value=HomeScreenState.Empty
                }
            }
        }
    }

    fun getPopularRestaurants(){

    }

    sealed class HomeScreenState{
        object Empty :HomeScreenState()
        object Loading:HomeScreenState()
        object Success:HomeScreenState()
    }

    sealed class HomeScreenNavigationEvents{
        object NavigateToDetail:HomeScreenNavigationEvents()
    }
}