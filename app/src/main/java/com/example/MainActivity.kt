package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.KafGuestHouseTheme
import com.example.ui.viewmodel.GuestHouseViewModel

enum class AppScreen {
    HOME,
    HISTORY
}

class MainActivity : ComponentActivity() {

    private val viewModel: GuestHouseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KafGuestHouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

                    Crossfade(
                        targetState = currentScreen,
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.HOME -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    uiState = uiState,
                                    onNavigateToHistory = {
                                        currentScreen = AppScreen.HISTORY
                                    }
                                )
                            }
                            AppScreen.HISTORY -> {
                                BackHandler {
                                    currentScreen = AppScreen.HOME
                                }
                                HistoryScreen(
                                    guests = uiState.guestsHistory,
                                    onBackClick = {
                                        currentScreen = AppScreen.HOME
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
