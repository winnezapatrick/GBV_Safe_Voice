package com.example.gbvapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.gbvapp.data.firebase.AuthRepository
import com.example.gbvapp.data.firebase.CurrentOfficer
import com.example.gbvapp.data.firebase.OfficerRole
import com.example.gbvapp.ui.screens.AdminHome
import com.example.gbvapp.ui.screens.OfficerHome
import com.example.gbvapp.ui.screens.OfficerLoginScreen
import com.example.gbvapp.ui.screens.OfficerRegisterScreen
import com.example.gbvapp.ui.screens.ReporterFinalScreen
import com.example.gbvapp.ui.screens.ReporterStep1ContextScreen
import com.example.gbvapp.ui.screens.ReporterStep2IncidentScreen
import com.example.gbvapp.ui.screens.ReporterStep3DetailsScreen
import com.example.gbvapp.ui.screens.ReporterStep4ReviewScreen
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
    var currentScreen by rememberSaveable { mutableStateOf("welcome") }

    // Reporter Form State (retained across rotation via rememberSaveable)
    var reporterReportingFor by rememberSaveable { mutableStateOf("") }
    var reporterCategory by rememberSaveable { mutableStateOf("") }
    var reporterDescription by rememberSaveable { mutableStateOf("") }
    var reporterEvidenceUriStrings by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var reporterIncidentDate by rememberSaveable { mutableStateOf("") }
    var reporterIncidentTime by rememberSaveable { mutableStateOf("") }
    var reporterLocation by rememberSaveable { mutableStateOf("") }
    var reporterSubmittedReference by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val activity = (context as? ComponentActivity)

    fun clearReporterState() {
        reporterReportingFor = ""
        reporterCategory = ""
        reporterDescription = ""
        reporterEvidenceUriStrings = emptyList()
        reporterIncidentDate = ""
        reporterIncidentTime = ""
        reporterLocation = ""
        reporterSubmittedReference = ""
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            "welcome" -> {
                WelcomeScreen(
                    onProceedToReport = { currentScreen = "step1" },
                    onOfficerTabSelected = { currentScreen = "officer_login" }
                )
            }
            "step1" -> {
                BackHandler { currentScreen = "welcome" }
                ReporterStep1ContextScreen(
                    initialReportingFor = reporterReportingFor,
                    onBack = { currentScreen = "welcome" },
                    onNext = { choice ->
                        reporterReportingFor = choice
                        currentScreen = "step2"
                    },
                    onQuickExit = {
                        clearReporterState()
                        currentScreen = "welcome"
                    }
                )
            }
            "step2" -> {
                BackHandler { currentScreen = "step1" }
                ReporterStep2IncidentScreen(
                    initialCategory = reporterCategory,
                    initialDescription = reporterDescription,
                    initialEvidenceUris = reporterEvidenceUriStrings.map { Uri.parse(it) },
                    onBack = { currentScreen = "step1" },
                    onNext = { cat, desc, uris ->
                        reporterCategory = cat
                        reporterDescription = desc
                        reporterEvidenceUriStrings = uris.map { it.toString() }
                        currentScreen = "step3"
                    },
                    onQuickExit = {
                        clearReporterState()
                        currentScreen = "welcome"
                    }
                )
            }
            "step3" -> {
                BackHandler { currentScreen = "step2" }
                ReporterStep3DetailsScreen(
                    initialDate = reporterIncidentDate,
                    initialTime = reporterIncidentTime,
                    initialLocation = reporterLocation,
                    onBack = { currentScreen = "step2" },
                    onNext = { date, time, loc ->
                        reporterIncidentDate = date
                        reporterIncidentTime = time
                        reporterLocation = loc
                        currentScreen = "step4"
                    },
                    onQuickExit = {
                        clearReporterState()
                        currentScreen = "welcome"
                    }
                )
            }
            "step4" -> {
                BackHandler { currentScreen = "step3" }
                ReporterStep4ReviewScreen(
                    reportingFor = reporterReportingFor,
                    category = reporterCategory,
                    description = reporterDescription,
                    evidenceUris = reporterEvidenceUriStrings.map { Uri.parse(it) },
                    incidentDate = reporterIncidentDate,
                    incidentTime = reporterIncidentTime,
                    location = reporterLocation,
                    onBack = { currentScreen = "step3" },
                    onSuccess = { ref ->
                        reporterSubmittedReference = ref
                        currentScreen = "final"
                    },
                    onQuickExit = {
                        clearReporterState()
                        currentScreen = "welcome"
                    }
                )
            }
            "final" -> {
                BackHandler {
                    clearReporterState()
                    currentScreen = "welcome"
                }
                ReporterFinalScreen(
                    caseReference = reporterSubmittedReference,
                    onDone = {
                        clearReporterState()
                        currentScreen = "welcome"
                    },
                    onOfficerTabSelected = {
                        clearReporterState()
                        currentScreen = "officer_login"
                    }
                )
            }
            "officer_login" -> {
                BackHandler { currentScreen = "welcome" }
                OfficerLoginScreen(
                    onLoginSuccess = {
                        if (CurrentOfficer.profile?.role == OfficerRole.ADMIN) {
                            currentScreen = "admin_home"
                        } else {
                            currentScreen = "officer_home"
                        }
                    },
                    onRegisterClick = { currentScreen = "officer_register" },
                    onReportTabSelected = { currentScreen = "welcome" }
                )
            }
            "officer_register" -> {
                BackHandler { currentScreen = "officer_login" }
                OfficerRegisterScreen(
                    onBackToLogin = { currentScreen = "officer_login" },
                    onReportTabSelected = { currentScreen = "welcome" }
                )
            }
            "officer_home" -> {
                BackHandler { activity?.finish() }
                OfficerHome(
                    onLogout = {
                        AuthRepository.logout()
                        currentScreen = "officer_login"
                    }
                )
            }
            "admin_home" -> {
                BackHandler { activity?.finish() }
                AdminHome(
                    onLogout = {
                        AuthRepository.logout()
                        currentScreen = "officer_login"
                    },
                    onQuickExit = {
                        clearReporterState()
                        currentScreen = "welcome"
                    }
                )
            }
        }
    }
}
