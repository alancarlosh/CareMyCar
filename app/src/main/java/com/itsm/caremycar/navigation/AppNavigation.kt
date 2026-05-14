package com.itsm.caremycar.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itsm.caremycar.R
import com.itsm.caremycar.screens.user.AddVehicle
import com.itsm.caremycar.screens.user.CarDetailsScreen
import com.itsm.caremycar.screens.user.ProductDetailsScreen
import com.itsm.caremycar.screens.user.UserScreen
import com.itsm.caremycar.screens.agency.*
import com.itsm.caremycar.session.Login
import com.itsm.caremycar.session.LoginViewModel
import com.itsm.caremycar.session.Register
import com.itsm.caremycar.session.AppStartViewModel

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val appStartViewModel: AppStartViewModel = hiltViewModel()
    val appStartUiState by appStartViewModel.uiState.collectAsState()

    if (!appStartUiState.isReady) {
        SessionSplashScreen()
        return
    }

    NavHost(navController = navController, startDestination = appStartUiState.startDestination) {

        composable("login") {
            val viewModel: LoginViewModel = hiltViewModel()
            Login(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = { role ->
                    val destination = if (role == "admin") "admin_screen" else "user_screen"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            Register(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("user_screen") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("user_screen") { backStackEntry ->
            val vehicleCreated by backStackEntry.savedStateHandle
                .getStateFlow("vehicle_created", false)
                .collectAsState()
            UserScreen(
                onAddVehicleClick = { navController.navigate("add_vehicle") },
                onVehicleClick = { vehicleId ->
                    navController.navigate("car_details/$vehicleId")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                shouldRefreshOnResume = vehicleCreated,
                onRefreshHandled = {
                    backStackEntry.savedStateHandle["vehicle_created"] = false
                }
            )
        }

        composable("add_vehicle") {
            AddVehicle(
                onBack = { navController.popBackStack() },
                onVehicleCreated = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("vehicle_created", true)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "car_details/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId").orEmpty()
            CarDetailsScreen(
                vehicleId = vehicleId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("product_details") {
            ProductDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // --- SECCIÓN AGENCY ---

        composable("admin_screen") {
            MenuAgency(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onNavigateToCatalog = { navController.navigate("agency_catalog") },
                onNavigateToOrders = { navController.navigate("agency_add_order") },
                onNavigateToSales = { navController.navigate("agency_orders") },
                onNavigateToReminders = { navController.navigate("agency_reminders") },
                onNavigateToServiceManagement = { navController.navigate("agency_service_orders") }
            )
        }

        composable("agency_catalog") { backStackEntry ->
            val partCreated by backStackEntry.savedStateHandle
                .getStateFlow("part_created", false)
                .collectAsState()
            val createdPartId by backStackEntry.savedStateHandle
                .getStateFlow("created_part_id", "")
                .collectAsState()
            val createdPartName by backStackEntry.savedStateHandle
                .getStateFlow("created_part_name", "")
                .collectAsState()
            val createdPartCategory by backStackEntry.savedStateHandle
                .getStateFlow("created_part_category", "")
                .collectAsState()
            val createdPartPrice by backStackEntry.savedStateHandle
                .getStateFlow("created_part_price", 0.0)
                .collectAsState()
            val createdPartQuantity by backStackEntry.savedStateHandle
                .getStateFlow("created_part_quantity", 0)
                .collectAsState()

            val optimisticCreatedPart = if (createdPartId.isBlank()) null else com.itsm.caremycar.vehicle.Part(
                id = createdPartId,
                userId = "",
                name = createdPartName,
                category = createdPartCategory,
                make = null,
                year = null,
                model = null,
                compatibility = emptyList(),
                price = createdPartPrice,
                quantity = createdPartQuantity,
                createdAt = null,
                updatedAt = null
            )

            CatalogoRefaccionesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddParts = { navController.navigate("agency_add_parts") },
                onNavigateToEditPart = { partId -> navController.navigate("agency_edit_part/$partId") },
                shouldRefreshOnResume = partCreated,
                onRefreshHandled = {
                    backStackEntry.savedStateHandle["part_created"] = false
                },
                optimisticCreatedPart = optimisticCreatedPart,
                onOptimisticPartHandled = {
                    backStackEntry.savedStateHandle["created_part_id"] = ""
                    backStackEntry.savedStateHandle["created_part_name"] = ""
                    backStackEntry.savedStateHandle["created_part_category"] = ""
                    backStackEntry.savedStateHandle["created_part_price"] = 0.0
                    backStackEntry.savedStateHandle["created_part_quantity"] = 0
                }
            )
        }

        composable("agency_add_parts") {
            AddPartsScreen(
                onNavigateBack = { navController.popBackStack() },
                onPartCreated = { part ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("part_created", true)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("created_part_id", part.id)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("created_part_name", part.name)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("created_part_category", part.category)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("created_part_price", part.price)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("created_part_quantity", part.quantity)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "agency_edit_part/{partId}",
            arguments = listOf(navArgument("partId") { type = NavType.StringType })
        ) { backStackEntry ->
            val partId = backStackEntry.arguments?.getString("partId").orEmpty()
            EditPartScreen(
                partId = partId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("agency_orders") {
            PedidosScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("agency_add_order") {
            AddOrderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("agency_reminders") {
            RemindersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("agency_service_orders") {
            AgencyServiceOrdersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun SessionSplashScreen() {
    val pulse = rememberInfiniteTransition(label = "splashPulse")
    val logoAlpha by pulse.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE9F5FC),
                        Color(0xFFD6ECF8),
                        Color(0xFFC4E4F5)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "CareMyCar",
                modifier = Modifier
                    .size(96.dp)
                    .alpha(logoAlpha)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "CareMyCar",
                color = Color(0xFF1E4E66),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Text(
                text = "Restaurando sesión...",
                color = Color(0xFF356C86),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
