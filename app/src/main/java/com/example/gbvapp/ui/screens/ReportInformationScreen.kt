package com.example.gbvapp.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun ReportInformationScreen(
    onBack: () -> Unit = {},
    onProceedToDetails: () -> Unit = {},
    onQuickExit: () -> Unit = {}
) {
    ReporterStep1ContextScreen(
        initialReportingFor = "Myself",
        onBack = onBack,
        onNext = { onProceedToDetails() },
        onQuickExit = onQuickExit
    )
}
