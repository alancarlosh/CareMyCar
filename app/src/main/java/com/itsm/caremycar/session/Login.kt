package com.itsm.caremycar.session

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.itsm.caremycar.R

@Composable
fun Login(
    viewModel: LoginViewModel = hiltViewModel(),
    sessionMessage: String? = null,
    onSessionMessageShown: () -> Unit = {},
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.isLoggedIn, key2 = uiState.user) {
        if (uiState.isLoggedIn && uiState.user != null) {
            onNavigateToHome(uiState.user!!.role ?: "user")
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(sessionMessage) {
        sessionMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            onSessionMessageShown()
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login_animation))
    val progress by animateLottieCompositionAsState(
        isPlaying = true,
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.5f
    )

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                modifier = Modifier.size(210.dp),
                composition = composition,
                progress = { progress }
            )

            AuthHero(
                eyebrow = "CareMyCar",
                title = "Bienvenido de vuelta",
                description = "Accede a tu cochera, mantenimientos y costos desde un solo lugar.",
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            AuthPanel {
                AuthTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError.isNotEmpty()) emailError = ""
                    },
                    label = emailError.ifEmpty { "Email" },
                    leadingIcon = Icons.Rounded.AccountCircle,
                    isError = emailError.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                AuthTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError.isNotEmpty()) passwordError = ""
                    },
                    label = passwordError.ifEmpty { "Contraseña" },
                    leadingIcon = Icons.Rounded.Lock,
                    isError = passwordError.isNotEmpty(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    trailingContent = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(2.dp))

                AuthSubmitButton(
                    text = "Ingresar",
                    onClick = {
                        keyboardController?.hide()
                        val trimmedEmail = email.trim()

                        if (trimmedEmail.isEmpty()) {
                            emailError = "El email es requerido"
                            return@AuthSubmitButton
                        }

                        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                            emailError = "Email inválido"
                            return@AuthSubmitButton
                        }

                        if (password.isBlank()) {
                            passwordError = "La contraseña es requerida"
                            return@AuthSubmitButton
                        }

                        viewModel.login(trimmedEmail, password)
                    },
                    enabled = !uiState.isLoading,
                    content = {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Ingresar")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            AuthFooter(
                prompt = "¿No eres miembro?",
                action = "Regístrate ahora",
                onActionClick = onNavigateToRegister
            )
        }
    }
}
