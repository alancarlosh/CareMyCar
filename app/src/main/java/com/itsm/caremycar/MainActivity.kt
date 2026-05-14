package com.itsm.caremycar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.itsm.caremycar.navigation.AppNavigation
import com.itsm.caremycar.session.UnauthorizedSessionNotifier
import com.itsm.caremycar.ui.theme.CareMyCarTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var unauthorizedSessionNotifier: UnauthorizedSessionNotifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CareMyCarTheme {
                AppNavigation(unauthorizedSessionEvents = unauthorizedSessionNotifier.events)
            }
        }
    }
}