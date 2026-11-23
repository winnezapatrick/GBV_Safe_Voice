package com.example.gbvapp.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun OfficerDashboardScreen(
    onOpenCase: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onReportTabSelected: () -> Unit = {},
    onQuickExit: () -> Unit = {}
) {
    OfficerHome(onLogout = onLogout)
}
