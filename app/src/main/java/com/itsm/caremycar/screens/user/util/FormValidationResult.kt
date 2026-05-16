package com.itsm.caremycar.screens.user.util

internal sealed interface FormValidationResult<out T> {
    data class Valid<T>(val value: T) : FormValidationResult<T>
    data class Invalid(val message: String) : FormValidationResult<Nothing>
}
