package com.itsm.caremycar.screens.user.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.itsm.caremycar.screens.user.ClientFeedbackEvent
import kotlinx.coroutines.flow.Flow

internal data class ClientFeedbackMessage(
    val text: String,
    val isError: Boolean
)

@Composable
internal fun rememberClientFeedback(
    events: Flow<ClientFeedbackEvent>,
    resetKey: Any? = Unit
): State<ClientFeedbackMessage?> {
    val feedback = remember { mutableStateOf<ClientFeedbackMessage?>(null) }

    LaunchedEffect(resetKey) {
        feedback.value = null
    }

    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is ClientFeedbackEvent.Message -> {
                    feedback.value = ClientFeedbackMessage(
                        text = event.text,
                        isError = event.isError
                    )
                }
                else -> Unit
            }
        }
    }

    return feedback
}

@Composable
internal fun ClientFeedbackText(
    message: ClientFeedbackMessage,
    modifier: Modifier = Modifier
) {
    Text(
        text = message.text,
        color = if (message.isError) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        modifier = modifier
    )
}
