package com.example.foodhub_app.ui.navigation

import kotlinx.serialization.Serializable

interface navRoutes

@Serializable
object SignUp:navRoutes

@Serializable
object Login:navRoutes

@Serializable
object Auth:navRoutes

@Serializable
object Home:navRoutes