package com.example.foodhub_app.ui.feature.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodhub_app.R
import com.example.foodhub_app.data.model.CartItem
import com.example.foodhub_app.data.model.CheckoutDetails
import com.example.foodhub_app.ui.feature.food_item_details.FoodItemCounter
import com.example.foodhub_app.utils.StringUtils
@Composable
fun CartScreen(navController: NavController,viewModel: CartScreenViewModel=hiltViewModel()){
    val uiState = viewModel.uiState.collectAsState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Spacer(modifier = Modifier.size(48.dp))
        CartScreenHeader()
        Spacer(modifier = Modifier.size(16.dp))
        when(uiState.value){
            is CartScreenViewModel.cartUiState.Error ->{

            }
            CartScreenViewModel.cartUiState.Loading ->{
                Spacer(modifier = Modifier.size(16.dp))
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.size(16.dp))
                    CircularProgressIndicator()
                }
            }
            is CartScreenViewModel.cartUiState.Nothing -> TODO()
            is CartScreenViewModel.cartUiState.Success ->{
                val data=(uiState.value as CartScreenViewModel.cartUiState.Success).data
                LazyColumn {
                    items(data.items){
                        CartItemView(cartItem = it,
                            decrementQuantity = {cartItem, quantity -> viewModel.decrementQuantity(cartItem)},
                            incrementQuantity = {cartItem, quantity -> viewModel.incrementQuantity(cartItem)},
                            onRemoveItem = {viewModel.removeItem(it)})
                    }
                    item{
                        CheckoutView(data.checkoutDetails)
                    }
                }
            }
        }
    }

}

@Composable
fun CheckoutView(checkoutDetails: CheckoutDetails){
    Column(modifier = Modifier.fillMaxWidth()){
        CheckoutItem(title = "Subtotal",value = checkoutDetails.subTotal,currency = "USD")
        CheckoutItem(title = "Tax",value = checkoutDetails.tax,currency = "USD")
        CheckoutItem(title = "Discount",value = checkoutDetails.deliveryFee,currency = "USD")
        CheckoutItem(title = "Total",value = checkoutDetails.totalAmount,currency = "USD")
    }
}

@Composable
fun CheckoutItem(title:String,value:Double,currency:String){
    Column {
        Row(modifier=Modifier.fillMaxWidth()){
            Text(text = title,style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = StringUtils.formatCurrency(value),style = MaterialTheme.typography.bodyLarge)
        }
        VerticalDivider()
    }
}

@Composable
fun CartScreenHeader(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically


    ){
        IconButton(onClick = {}) {
            Image(painter = painterResource(id = R.drawable.back_button), contentDescription = null,modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "Cart", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun CartItemView(
    cartItem: CartItem,
    decrementQuantity:(cartItem: CartItem,count: Int)->Unit,
    incrementQuantity:(cartItem: CartItem,count: Int)->Unit,
    onRemoveItem:(cartItem: CartItem)->Unit
){
    Row(modifier = Modifier.fillMaxWidth()){
        AsyncImage(
            model = cartItem.menuItemId.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically){
                Text(text = cartItem.menuItemId.name,style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {onRemoveItem.invoke(cartItem)}) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = null,modifier = Modifier.size(24.dp),tint = MaterialTheme.colorScheme.primary)
                }
            }
            Text(
                text="${cartItem.menuItemId.description}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color =Color.Gray
            )
            Spacer(modifier = Modifier.size(16.dp))
            Row(){
                Text(text = "${cartItem.menuItemId.price}",style = MaterialTheme.typography.bodyLarge,color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.weight(1f))
                FoodItemCounter(
                    cartItem.quantity,
                    decrementQuantity = {decrementQuantity.invoke(cartItem,cartItem.quantity)},
                    incrementQuantity = {incrementQuantity.invoke(cartItem,cartItem.quantity)}
                )
            }
    }


    }

}