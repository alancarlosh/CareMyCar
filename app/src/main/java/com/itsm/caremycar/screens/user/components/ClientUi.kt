package com.itsm.caremycar.screens.user.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector

internal val ClientInk = Color(0xFF102A43)
internal val ClientBlue = Color(0xFF0B6E8A)
internal val ClientSky = Color(0xFFE9F6FA)
internal val ClientMint = Color(0xFFBDEBDD)
internal val ClientAmber = Color(0xFFF7C76E)
internal val ClientCanvas = Color(0xFFF7FAFC)
internal val ClientSurface = Color.White
internal val ClientSurfaceMuted = Color(0xFFF1F6F9)
internal val ClientDanger = Color(0xFFC2413A)
internal val ClientSuccess = Color(0xFF207A5A)

@Composable
internal fun ClientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ClientCanvas)
    ) {
        content()
    }
}

@Composable
internal fun ClientMetricChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.82f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
            Text(
                text = "$label ",
                style = MaterialTheme.typography.labelMedium,
                color = ClientInk.copy(alpha = 0.62f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ClientInk
            )
        }
    }
}

internal enum class ClientBadgeTone {
    Neutral,
    Info,
    Success,
    Warning,
    Danger
}

@Composable
internal fun ClientStatusBadge(
    text: String,
    tone: ClientBadgeTone,
    modifier: Modifier = Modifier
) {
    val (background, foreground) = when (tone) {
        ClientBadgeTone.Neutral -> ClientSurfaceMuted to ClientInk
        ClientBadgeTone.Info -> ClientSky to ClientBlue
        ClientBadgeTone.Success -> ClientMint.copy(alpha = 0.58f) to ClientSuccess
        ClientBadgeTone.Warning -> ClientAmber.copy(alpha = 0.34f) to ClientInk
        ClientBadgeTone.Danger -> ClientDanger.copy(alpha = 0.12f) to ClientDanger
    }
    Surface(
        modifier = modifier,
        color = background,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = foreground
        )
    }
}

@Composable
internal fun ClientSectionHeader(
    eyebrow: String,
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = eyebrow.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = ClientBlue
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = ClientInk
        )
        description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = ClientInk.copy(alpha = 0.68f)
            )
        }
    }
}

@Composable
internal fun ClientStepHeader(
    step: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Surface(
            color = ClientBlue.copy(alpha = 0.12f),
            shape = RoundedCornerShape(999.dp)
        ) {
            Text(
                text = step,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelMedium,
                color = ClientBlue
            )
        }
        Box(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = ClientInk
        )
    }
}

@Composable
internal fun ClientHeroMetric(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ClientMint.copy(alpha = 0.42f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ClientInk.copy(alpha = 0.66f),
                modifier = Modifier.width(18.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = ClientInk
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = ClientInk.copy(alpha = 0.62f)
            )
        }
    }
}

@Composable
internal fun ClientPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ClientSurface,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
        shadowElevation = 1.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
internal fun ClientEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ClientSurfaceMuted,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = ClientInk
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = ClientInk.copy(alpha = 0.68f)
            )
        }
    }
}

@Composable
internal fun ClientVehicleImagePlaceholder(
    modifier: Modifier = Modifier,
    label: String = "Sin imagen disponible"
) {
    Box(
        modifier = modifier
            .background(ClientSky),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = ClientSurface,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DirectionsCar,
                    contentDescription = null,
                    tint = ClientBlue,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = ClientInk.copy(alpha = 0.62f)
            )
        }
    }
}

@Composable
internal fun ClientInlineAlert(
    text: String,
    tone: ClientBadgeTone,
    modifier: Modifier = Modifier
) {
    val (background, foreground) = when (tone) {
        ClientBadgeTone.Neutral -> ClientSurfaceMuted to ClientInk
        ClientBadgeTone.Info -> ClientSky to ClientBlue
        ClientBadgeTone.Success -> ClientMint.copy(alpha = 0.42f) to ClientSuccess
        ClientBadgeTone.Warning -> ClientAmber.copy(alpha = 0.24f) to ClientInk
        ClientBadgeTone.Danger -> ClientDanger.copy(alpha = 0.1f) to ClientDanger
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = foreground
        )
    }
}

@Composable
internal fun ClientLoadingPanel(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    ClientPanel(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(22.dp)
                        .height(22.dp),
                    strokeWidth = 2.dp,
                    color = ClientBlue
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = ClientInk
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ClientInk.copy(alpha = 0.68f)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(ClientSurfaceMuted, RoundedCornerShape(999.dp))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(12.dp)
                    .background(ClientSurfaceMuted, RoundedCornerShape(999.dp))
            )
        }
    }
}

@Composable
internal fun ClientDialog(
    title: String,
    onDismissRequest: () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = ClientInk
            )
        },
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        shape = RoundedCornerShape(24.dp),
        containerColor = ClientSurface
    )
}

@Composable
internal fun ClientPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingContent: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ClientBlue,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
    ) {
        leadingContent?.invoke()
        Text(text)
    }
}

@Composable
internal fun ClientSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ClientSurfaceMuted,
            contentColor = ClientInk
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(text)
    }
}

@Composable
internal fun ClientDialogAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tone: ClientBadgeTone = ClientBadgeTone.Neutral,
    leadingContent: (@Composable () -> Unit)? = null
) {
    val container = when (tone) {
        ClientBadgeTone.Danger -> ClientDanger.copy(alpha = 0.12f)
        ClientBadgeTone.Success -> ClientMint.copy(alpha = 0.44f)
        ClientBadgeTone.Warning -> ClientAmber.copy(alpha = 0.28f)
        ClientBadgeTone.Info -> ClientSky
        ClientBadgeTone.Neutral -> ClientSurfaceMuted
    }
    val content = when (tone) {
        ClientBadgeTone.Danger -> ClientDanger
        ClientBadgeTone.Success -> ClientSuccess
        ClientBadgeTone.Info -> ClientBlue
        else -> ClientInk
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        leadingContent?.invoke()
        Text(text)
    }
}

@Composable
internal fun ClientQuantityStepper(
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    canDecrease: Boolean,
    canIncrease: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ClientSurfaceMuted,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(6.dp)
        ) {
            IconButton(onClick = onDecrease, enabled = canDecrease) {
                Icon(Icons.Default.Remove, contentDescription = "Disminuir")
            }
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = ClientInk,
                modifier = Modifier.width(28.dp)
            )
            IconButton(onClick = onIncrease, enabled = canIncrease) {
                Icon(Icons.Default.Add, contentDescription = "Aumentar")
            }
        }
    }
}

@Composable
internal fun clientFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ClientInk,
    unfocusedTextColor = ClientInk,
    disabledTextColor = ClientInk.copy(alpha = 0.46f),
    focusedBorderColor = ClientBlue,
    unfocusedBorderColor = ClientInk.copy(alpha = 0.22f),
    disabledBorderColor = ClientInk.copy(alpha = 0.14f),
    focusedLabelColor = ClientBlue,
    unfocusedLabelColor = ClientInk.copy(alpha = 0.66f),
    disabledLabelColor = ClientInk.copy(alpha = 0.38f),
    focusedTrailingIconColor = ClientBlue,
    unfocusedTrailingIconColor = ClientInk.copy(alpha = 0.58f),
    disabledTrailingIconColor = ClientInk.copy(alpha = 0.3f),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color.White,
    cursorColor = ClientBlue
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ClientTopAppBar(
    title: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = ClientBlue,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = { Text(title) },
        navigationIcon = navigationIcon,
        actions = actions
    )
}
