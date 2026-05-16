package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.screens.user.components.ClientBadgeTone
import com.itsm.caremycar.screens.user.components.ClientHeroMetric
import com.itsm.caremycar.screens.user.components.ClientInlineAlert
import com.itsm.caremycar.screens.user.components.ClientInk
import com.itsm.caremycar.screens.user.components.ClientPanel
import com.itsm.caremycar.screens.user.components.ClientPrimaryButton
import com.itsm.caremycar.screens.user.components.ClientSectionHeader
import com.itsm.caremycar.screens.user.components.clientFieldColors
import com.itsm.caremycar.screens.user.util.formatMxn
import java.util.Locale

@Composable
fun MonthlyCostContent(
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: MonthlyCostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var monthlyKm by remember { mutableStateOf("1200") }
    var kmPerLiter by remember { mutableStateOf("13") }
    var fuelPrice by remember { mutableStateOf("24.5") }
    var maintenancePerKm by remember { mutableStateOf("0.8") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ClientPanel {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ClientSectionHeader(
                        eyebrow = "Estimador",
                        title = "Costo mensual",
                        description = "Calcula cuánto cuesta usar tu auto al mes con combustible y mantenimiento."
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ClientHeroMetric(
                            icon = Icons.Outlined.Route,
                            label = "Km / mes",
                            value = monthlyKm,
                            modifier = Modifier.weight(1f)
                        )
                        ClientHeroMetric(
                            icon = Icons.Outlined.Payments,
                            label = "Total",
                            value = uiState.estimate?.let { formatMxn(it.totalMonthlyCost) } ?: "-",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        uiState.error?.let { error ->
            item {
                ClientInlineAlert(
                    text = error,
                    tone = ClientBadgeTone.Danger
                )
            }
        }

        item {
            ClientPanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ClientSectionHeader(
                        eyebrow = "Datos",
                        title = "Parámetros de cálculo"
                    )
                    CostNumberField("Kilómetros al mes", monthlyKm) { monthlyKm = it }
                    CostNumberField("Rendimiento (km/l)", kmPerLiter) { kmPerLiter = it }
                    CostNumberField("Precio por litro", fuelPrice) { fuelPrice = it }
                    CostNumberField("Mantenimiento por km", maintenancePerKm) { maintenancePerKm = it }
                    ClientPrimaryButton(
                        text = "Calcular costo mensual",
                        onClick = {
                            viewModel.calculate(
                                monthlyKm = monthlyKm,
                                kmPerLiter = kmPerLiter,
                                fuelPrice = fuelPrice,
                                maintenancePerKm = maintenancePerKm
                            )
                        },
                        enabled = !uiState.isCalculating,
                        modifier = Modifier.fillMaxWidth(),
                        leadingContent = {
                            if (uiState.isCalculating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(end = 8.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    )
                }
            }
        }

        uiState.estimate?.let { estimate ->
            item {
                ClientPanel {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ClientSectionHeader(
                            eyebrow = "Resultado",
                            title = formatMxn(estimate.totalMonthlyCost),
                            description = "Costo mensual estimado"
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ClientHeroMetric(
                                icon = Icons.Outlined.LocalGasStation,
                                label = "Litros",
                                value = String.format(Locale.US, "%.2f", estimate.litersNeeded),
                                modifier = Modifier.weight(1f)
                            )
                            ClientHeroMetric(
                                icon = Icons.Outlined.Payments,
                                label = "Combustible",
                                value = formatMxn(estimate.fuelCost),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Text(
                            text = "Mantenimiento estimado: ${formatMxn(estimate.maintenanceCost)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ClientInk
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CostNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            onValueChange(input.filter { it.isDigit() || it == '.' }.take(10))
        },
        label = { Text(label) },
        colors = clientFieldColors(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}
