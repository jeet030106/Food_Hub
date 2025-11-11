package com.example.foodhub_app.ui.feature.order_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodhub_app.data.model.Order
import com.example.foodhub_app.ui.navigation.OrderDetail
import com.example.foodhub_app.ui.theme.Primary
import kotlinx.coroutines.launch

@Composable
fun OrderListScreen(navController: NavController,viewModel: OrderListViewModel= hiltViewModel()) {
    val types= viewModel.getOrderTypes()
    val coroutineScope=rememberCoroutineScope()
    val pager= rememberPagerState(pageCount = {types.size})
    val uiState=viewModel.uiStates.collectAsStateWithLifecycle()
    LaunchedEffect(pager.currentPage) {
        viewModel.getOrderListByTypes(types[pager.currentPage])
    }
    Column(modifier = Modifier.fillMaxSize()) {

        Text(text = "Order List",modifier = Modifier.fillMaxWidth().padding(8.dp),
            style= MaterialTheme.typography.bodyLarge)


        ScrollableTabRow(
            selectedTabIndex = pager.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            types.forEachIndexed { index, type ->
                Text(text = type
                    ,modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable{
                            coroutineScope.launch {
                                pager.animateScrollToPage(index)
                            }
                        },
                    style= MaterialTheme.typography.bodyLarge
                )
            }
        }
        HorizontalPager(state = pager) {page->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                when(uiState.value){
                    is OrderListViewModel.states.loading->{
                        CircularProgressIndicator()
                    }
                    is OrderListViewModel.states.success->{
                        val orders=(uiState.value as OrderListViewModel.states.success).data
                        LazyColumn{
                            items(orders){order->
                                OrderListItem(order){
                                    navController.navigate(OrderDetail(orderId = order.id))
                                }
                            }
                        }
                    }
                    else->{
                        Text("There is an Error,, Try Again!!")
                    }
                }
            }
        }
    }
}
@Composable
fun OrderListItem(order: Order, onOrderClick:()->Unit){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clip(RoundedCornerShape(12.dp))
        .padding(4.dp)
        .clickable(onClick = onOrderClick)
        .background(color = Primary.copy(alpha = 0.1f))
    ){
        Text(text = order.id)
        Text(text = order.status)
        Text(text = order.address.addressLine1)
    }
}