package com.itsm.caremycar.session

import androidx.lifecycle.ViewModel
import com.itsm.caremycar.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AppStartUiState(
    val isReady: Boolean = false,
    val startDestination: String = "login"
)

@HiltViewModel
class AppStartViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppStartUiState())
    val uiState: StateFlow<AppStartUiState> = _uiState.asStateFlow()

    init {
        resolveStartDestination()
    }

    private fun resolveStartDestination() {
        val start = if (authRepository.isLoggedIn()) {
            if (authRepository.getSavedUserRole().equals("admin", ignoreCase = true)) {
                "admin_screen"
            } else {
                "user_screen"
            }
        } else {
            "login"
        }
        _uiState.value = AppStartUiState(
            isReady = true,
            startDestination = start
        )
    }
}
