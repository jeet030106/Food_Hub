package com.example.foodhub_app.ui.feature.orders

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodhub_app.data.model.Order
import com.example.foodhub_app.ui.theme.Primary
import kotlinx.coroutines.launch
import java.util.Collections.list
import com.example.foodhub_app.R
import com.example.foodhub_app.ui.navigation.OrderDetail
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OrderListScreen(navController: NavController,viewModel: OrderListViewModel=hiltViewModel()) {
    LaunchedEffect(key1=true) {
        viewModel.events.collectLatest {
            when(it){
                is OrderListViewModel.OrderEvents.NavigateToBack->{
                    navController.popBackStack()
                }
                is OrderListViewModel.OrderEvents.NavigateToDetail->{
                    navController.navigate(OrderDetail(it.order.id))
                }
            }
        }
    }
    Column (modifier = Modifier.fillMaxSize().padding(16.dp),horizontalAlignment = Alignment.CenterHorizontally){
        Row(
            modifier = Modifier.fillMaxWidth().padding( 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Image(painter = painterResource(id = R.drawable.back_button), contentDescription = "Back",modifier = Modifier.size(36.dp).clip(CircleShape).clickable{viewModel.navigateToBack()})
            Text(text="Orders",style=MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(48.dp))
        }
        val uiState=viewModel.uiState.collectAsState()
        when(uiState.value){
            is OrderListViewModel.OrderState.Loading->{
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
            }

            is OrderListViewModel.OrderState.OrderList->{
                val orders=(uiState.value as OrderListViewModel.OrderState.OrderList).orderList
                if(orders.isEmpty()){
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text="No orders found",color=Color.Gray,style=MaterialTheme.typography.bodyLarge)
                    }
                }else{
                    val listOfTabs=listOf("Upcoming","History")
                    val coroutineScope=rememberCoroutineScope()
                    val pager= rememberPagerState(pageCount = {listOfTabs.size},initialPage = 0)
                    TabRow(
                        selectedTabIndex = pager.currentPage,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(color=Color.White)
                            .border(width = 1.dp, color = Color.Gray,shape = RoundedCornerShape(16.dp)),
                        indicator = {tabPositions->},
                        divider = { },
                    ){
                        listOfTabs.forEachIndexed { index, title ->
                            Tab(
                                text={Text(text=title,color=if(pager.currentPage==index) Color.White else Color.Gray)},
                                selected=pager.currentPage==index,
                                onClick = {
                                    coroutineScope.launch {
                                        pager.animateScrollToPage(index)
                                    }
                                },
                                modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(color=if(pager.currentPage==index) Primary else Color.White),
                            )
                        }
                    }
                    HorizontalPager(state = pager) {
                        when(it){
                            0->{
                                OrderListInternal(
                                    orders.filter { it.status=="PENDING_ACCEPTANCE" }
                                ) {
                                    viewModel.navigateToDetail(it)
                                }
                            }
                            1->{
                                OrderListInternal(
                                    orders.filter { it.status!="PENDING_ACCEPTANCE" }
                                ){
                                    viewModel.navigateToDetail(it)
                                }
                            }
                        }
                    }
                }
            }

            is OrderListViewModel.OrderState.Error->{

            }
        }
    }
}

@Composable
fun OrderListInternal(list:List<Order>,onClick:(Order)->Unit){
    if(list.isEmpty()){
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Text(text="No orders found",color=Color.Gray,style=MaterialTheme.typography.bodyLarge)
        }
    }else{
        LazyColumn{
            items(list){
                OrderListItem(order = it,onCLick = {onClick(it)})
            }
        }
    }
}

@Composable
fun OrderListItem(order: Order,onCLick:()->Unit){
    val displayOrderId = if (order.id.length > 5) {
        order.id.take(5) + "..."
    } else order.id
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top row: Logo + Order ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = order.restaurant.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .shadow(6.dp, RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${order.items.size} Items",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = order.restaurant.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = displayOrderId,
                    fontSize = 14.sp,
                    color = Color(0xFFE57373),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Food status
            Text(
                text = "Food Status : ${order.status}",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {onCLick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "View Details")
                }
            }
        }
    }
}