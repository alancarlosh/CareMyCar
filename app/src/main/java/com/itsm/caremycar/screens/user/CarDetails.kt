package com.itsm.caremycar.screens.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.itsm.caremycar.screens.user.components.ClientBackground
import com.itsm.caremycar.screens.user.components.ClientBlue
import com.itsm.caremycar.screens.user.components.ClientSurface
import com.itsm.caremycar.screens.user.components.ClientTopAppBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsScreen(
    vehicleId: String,
    onBack: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    ClientBackground {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ClientTopAppBar(
                title = "Detalle del vehículo",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    TextButton(onClick = onBack) {
                        Text("Cerrar")
                    }
                }
            )
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                containerColor = ClientSurface,
                contentColor = ClientBlue,
                indicator = {},
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Editar vehículo") },
                    modifier = Modifier
                        .padding(4.dp)
                        .then(
                            if (selectedTab == 0) {
                                Modifier.background(
                                    color = ClientBlue.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Mantenimiento") },
                    modifier = Modifier
                        .padding(4.dp)
                        .then(
                            if (selectedTab == 1) {
                                Modifier.background(
                                    color = ClientBlue.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                )
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "car-detail-tab"
            ) { tab ->
                if (tab == 0) {
                    CarEditVehicleContent(vehicleId = vehicleId)
                } else {
                    CarMaintenanceContent(vehicleId = vehicleId)
                }
            }
        }
    }
    }
}
