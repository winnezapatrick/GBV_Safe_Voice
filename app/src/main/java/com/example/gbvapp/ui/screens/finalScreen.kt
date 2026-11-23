package com.example.gbvapp.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun FinalScreen(
    caseCode: String = "",
    onDone: () -> Unit = {},
    onOfficerTabSelected: () -> Unit = {},
    onQuickExit: () -> Unit = {}
) {
    ReporterFinalScreen(
        caseReference = caseCode,
        onDone = onDone,
        onOfficerTabSelected = onOfficerTabSelected
    )
}
