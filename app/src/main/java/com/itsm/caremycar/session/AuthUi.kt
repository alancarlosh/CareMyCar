package com.itsm.caremycar.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.itsm.caremycar.screens.user.components.ClientBlue
import com.itsm.caremycar.screens.user.components.ClientCanvas
import com.itsm.caremycar.screens.user.components.ClientInk
import com.itsm.caremycar.screens.user.components.ClientSky
import com.itsm.caremycar.screens.user.components.ClientSurface

@Composable
internal fun AuthBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ClientSky.copy(alpha = 0.95f),
                        ClientCanvas,
                        ClientSurface
                    )
                )
            )
    ) {
        content()
    }
}

@Composable
internal fun AuthHero(
    eyebrow: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = ClientBlue.copy(alpha = 0.1f),
            shape = RoundedCornerShape(999.dp)
        ) {
            Text(
                text = eyebrow.uppercase(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = ClientBlue,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = ClientInk
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = ClientInk.copy(alpha = 0.68f)
        )
    }
}

@Composable
internal fun AuthPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ClientSurface,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            content()
        }
    }
}

@Composable
internal fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            androidx.compose.material3.Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        trailingIcon = trailingContent,
        isError = isError,
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = ClientInk,
            unfocusedTextColor = ClientInk,
            disabledTextColor = ClientInk.copy(alpha = 0.45f),
            focusedBorderColor = ClientBlue,
            unfocusedBorderColor = ClientInk.copy(alpha = 0.18f),
            focusedLabelColor = ClientBlue,
            unfocusedLabelColor = ClientInk.copy(alpha = 0.62f),
            focusedLeadingIconColor = ClientBlue,
            unfocusedLeadingIconColor = ClientInk.copy(alpha = 0.48f),
            focusedTrailingIconColor = ClientBlue,
            unfocusedTrailingIconColor = ClientInk.copy(alpha = 0.48f),
            cursorColor = ClientBlue,
            focusedContainerColor = ClientSurface,
            unfocusedContainerColor = ClientSurface
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
internal fun AuthSubmitButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ClientBlue,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 15.dp)
    ) {
        content?.invoke() ?: Text(text)
    }
}

@Composable
internal fun AuthFooter(
    prompt: String,
    action: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$prompt ",
            style = MaterialTheme.typography.bodyMedium,
            color = ClientInk.copy(alpha = 0.7f)
        )
        Text(
            text = action,
            style = MaterialTheme.typography.bodyMedium,
            color = ClientBlue,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(start = 2.dp)
                .clickable(onClick = onActionClick)
        )
    }
}
