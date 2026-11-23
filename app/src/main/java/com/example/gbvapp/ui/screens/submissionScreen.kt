package com.example.gbvapp.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun SubmissionScreen(
    selectedCategory: String = "",
    onBack: () -> Unit = {},
    onSubmitFinalReport: () -> Unit = {},
    onQuickExit: () -> Unit = {}
) {
    ReporterStep4ReviewScreen(
        reportingFor = "Myself",
        category = selectedCategory,
        description = "",
        evidenceUris = emptyList(),
        incidentDate = "",
        incidentTime = "",
        location = "",
        onBack = onBack,
        onSuccess = { onSubmitFinalReport() },
        onQuickExit = onQuickExit
    )
}
