package com.example.gbvapp.ui.screens

import android.net.Uri
import androidx.compose.runtime.Composable

@Composable
fun IncidentCategoryScreen(
    onBack: () -> Unit = {},
    onSubmitReport: (category: String, description: String, evidenceUris: List<Uri>) -> Unit = { _, _, _ -> },
    onQuickExit: () -> Unit = {}
) {
    ReporterStep2IncidentScreen(
        initialCategory = "",
        initialDescription = "",
        initialEvidenceUris = emptyList(),
        onBack = onBack,
        onNext = { category, description, uris -> onSubmitReport(category, description, uris) },
        onQuickExit = onQuickExit
    )
}
