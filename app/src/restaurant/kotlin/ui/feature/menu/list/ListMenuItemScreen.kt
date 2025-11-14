package com.example.foodhub_app.ui.feature.menu.list

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodhub_app.ui.feature.auth.common.FoodItems
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ListMenuItemScreen(navController: NavController,animatedVisibilityScope: AnimatedVisibilityScope,viewModel: ListMenuViewModel= hiltViewModel()){
    val uiStates=viewModel.uiStates.collectAsStateWithLifecycle()
    LaunchedEffect(key1=true) {
        viewModel.events.collectLatest {
            when(it){
                is ListMenuViewModel.Events.addItem->{

                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when(val state=uiStates.value){
            is ListMenuViewModel.states.loading->{
                CircularProgressIndicator()
            }
            is ListMenuViewModel.states.error->{
                Text(text = (uiStates.value as ListMenuViewModel.states.error).msg)
            }
            is ListMenuViewModel.states.success->{
                LazyVerticalGrid(columns = GridCells.Fixed(2)){
                    items(state.data, key = { it.id!! }){ item->
                        FoodItems(foodItem = item,animatedVisibilityScope = animatedVisibilityScope) {

                        }

                    }
                }
            }
        }
    }
}