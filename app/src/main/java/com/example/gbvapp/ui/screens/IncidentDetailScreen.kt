package com.example.gbvapp.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun IncidentDetailScreen(
    caseReference: String,
    onBack: () -> Unit = {},
    onQuickExit: () -> Unit = {}
) {
    OfficerCaseDetailsScreen(
        reference = caseReference,
        onBack = onBack
    )
}
