package com.itsm.caremycar.screens.user

internal interface ClientFeedbackEvent {
    data class Message(
        val text: String,
        val isError: Boolean
    ) : ClientFeedbackEvent
}
