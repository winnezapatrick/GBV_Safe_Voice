package com.example.gbvapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.gbvapp.ui.screens.IncidentCategoryScreen
import com.example.gbvapp.ui.screens.IncidentDetailScreen
import com.example.gbvapp.ui.screens.ReportInformationScreen
import com.example.gbvapp.ui.screens.SubmissionScreen
import com.example.gbvapp.ui.screens.WelcomeScreen
import com.example.gbvapp.ui.theme.GBVAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GBVAPPTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent() {
    var currentScreen by remember { mutableStateOf("welcome") }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            "welcome" -> WelcomeScreen(
                onProceedToReport = { currentScreen = "step1" },
                onAdminTabSelected = { currentScreen = "incident" },
                onOfficerTabSelected = { currentScreen = "incident" }
            )
            "step1" -> ReportInformationScreen(
                onBack = { currentScreen = "welcome" },
                onProceedToDetails = { currentScreen = "step2" },
                onQuickExit = { /* Quick exit action */ }
            )
            "step2" -> IncidentCategoryScreen(
                onBack = { currentScreen = "step1" },
                onSubmitReport = { currentScreen = "step3" },
                onQuickExit = { /* Quick exit action */ }
            )
            "step3" -> SubmissionScreen(
                onBack = { currentScreen = "step2" },
                onSubmitFinalReport = { currentScreen = "incident" },
                onQuickExit = { /* Quick exit action */ }
            )
            "incident" -> IncidentDetailScreen(
                onBack = { currentScreen = "welcome" }
            )
        }
    }
}
