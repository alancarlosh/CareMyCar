package com.itsm.caremycar.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LogoutUiState(
    val isLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogoutUiState())
    val uiState: StateFlow<LogoutUiState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoggingOut = true)
            authRepository.logout()
            _uiState.value = LogoutUiState(
                isLoggingOut = false,
                isLoggedOut = true
            )
        }
    }

    fun consumeLoggedOut() {
        _uiState.value = _uiState.value.copy(isLoggedOut = false)
    }
}
