package com.example.foodhub_app.ui.feature.cart

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodhub_app.R
import com.example.foodhub_app.data.model.Address
import com.example.foodhub_app.data.model.CartItem
import com.example.foodhub_app.data.model.CheckoutDetails
import com.example.foodhub_app.ui.feature.food_item_details.FoodItemCounter
import com.example.foodhub_app.ui.navigation.AddressList
import com.example.foodhub_app.ui.navigation.Cart
import com.example.foodhub_app.ui.navigation.OrderSuccess
import com.example.foodhub_app.ui.theme.Primary
import com.example.foodhub_app.utils.StringUtils
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartScreen(navController: NavController, viewModel: CartScreenViewModel) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val paymentSheet = rememberPaymentSheet { paymentResult: PaymentSheetResult ->
        Log.d("Payment", "PaymentSheet result -> $paymentResult")
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                viewModel.onPaymentSuccess()
            }
            is PaymentSheetResult.Canceled -> {
                viewModel.onPaymentFailed()
            }
            is PaymentSheetResult.Failed -> {
                viewModel.onPaymentFailed()
            }
        }
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val current = navController.currentBackStackEntry?.destination?.route ?: "null"
            val previous = navController.previousBackStackEntry?.destination?.route ?: "null"
            Log.d("NavStack", "Current route: $current, Previous route: $previous")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getaddresses()
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is CartScreenViewModel.cartEvents.onAddressSelect -> {
                    navController.navigate(AddressList)
                }

                is CartScreenViewModel.cartEvents.OnInitiatePayment -> {
                    PaymentConfiguration.init(context, event.data.publishableKey)

                    val customer = PaymentSheet.CustomerConfiguration(
                        event.data.customerId,
                        event.data.ephemeralKeySecret
                    )
                    val paymentSheetConfig = PaymentSheet.Configuration(
                        merchantDisplayName = "FoodHub",
                        customer = customer,
                        allowsDelayedPaymentMethods = false,
                    )

                    paymentSheet.presentWithPaymentIntent(
                        paymentIntentClientSecret = event.data.paymentIntentClientSecret,
                        configuration = paymentSheetConfig
                    )
                }

                is CartScreenViewModel.cartEvents.OrderSuccess -> {
                    event.orderId?.let { id ->
                        navController.navigate(OrderSuccess(id)) {
                            popUpTo(Cart) { inclusive = true }
                            launchSingleTop = true
                        }
                    } ?: Log.e("Payment", "OrderId is null, navigation skipped")
                }

                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            CartScreenHeader(onBackClick = { navController.popBackStack() })
            Spacer(modifier = Modifier.height(12.dp))

            when (val state = uiState.value) {
                is CartScreenViewModel.cartUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Failed to load cart items", color = Color.Red)
                    }
                }
                CartScreenViewModel.cartUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is CartScreenViewModel.cartUiState.Nothing -> {}
                is CartScreenViewModel.cartUiState.Success -> {
                    val data = state.data
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 180.dp), // Space for sticky bottom bar
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(data.items, key = { it.menuItemId.id!! }) { item ->
                            CartItemView(
                                cartItem = item,
                                decrementQuantity = { cartItem, _ -> viewModel.decrementQuantity(cartItem) },
                                incrementQuantity = { cartItem, _ -> viewModel.incrementQuantity(cartItem) },
                                onRemoveItem = { viewModel.removeItem(it) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            CheckoutView(data.checkoutDetails)
                        }
                    }
                }
            }
        }

        //  Sticky Bottom Section (Address Selector + Checkout Button)
        if (uiState.value is CartScreenViewModel.cartUiState.Success) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    AddressSelect(address = null) {
                        viewModel.onAddressSelect()
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.checkout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Checkout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

//  Separate Card for Each Cart Item
@Composable
fun CartItemView(
    cartItem: CartItem,
    decrementQuantity: (cartItem: CartItem, count: Int) -> Unit,
    incrementQuantity: (cartItem: CartItem, count: Int) -> Unit,
    onRemoveItem: (cartItem: CartItem) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image
            AsyncImage(
                model = cartItem.menuItemId.imageUrl,
                contentDescription = cartItem.menuItemId.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = cartItem.menuItemId.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { onRemoveItem(cartItem) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove Item",
                            modifier = Modifier.size(18.dp),
                            tint = Color.Gray
                        )
                    }
                }

                Text(
                    text = cartItem.menuItemId.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$${cartItem.menuItemId.price}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )

                    FoodItemCounter(
                        quantity = cartItem.quantity,
                        decrementQuantity = { decrementQuantity(cartItem, cartItem.quantity) },
                        incrementQuantity = { incrementQuantity(cartItem, cartItem.quantity) }
                    )
                }
            }
        }
    }
}

//  Order Summary Receipt Card
@Composable
fun CheckoutView(checkoutDetails: CheckoutDetails) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp)),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Order Summary",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            HorizontalDivider(color = Color(0xFFF3F4F6))

            CheckoutItem(title = "Subtotal", value = checkoutDetails.subTotal)
            CheckoutItem(title = "Tax", value = checkoutDetails.tax)
            CheckoutItem(title = "Delivery fee", value = checkoutDetails.deliveryFee)

            HorizontalDivider(color = Color(0xFFE5E7EB))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = StringUtils.formatCurrency(checkoutDetails.totalAmount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary
                )
            }
        }
    }
}

@Composable
fun CheckoutItem(title: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = StringUtils.formatCurrency(value),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151)
        )
    }
}

// 🔹 Header Component
@Composable
fun CartScreenHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onBackClick() },
            modifier = Modifier.size(40.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.back_button),
                contentDescription = "Back",
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Cart",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
    }
}

// 🔹 Address Selection Card Component
@Composable
fun AddressSelect(address: Address?, onAddressSelect: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onAddressSelect),
        color = Color(0xFFF3F4F6),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = Primary.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (address != null) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = address.addressLine1,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "${address.city}, ${address.state}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            } else {
                Text(
                    text = "Select Delivery Address",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}