package com.example.gbvapp.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun AdminDashboardScreen(
    onOpenCase: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onOpenApprovals: () -> Unit = {},
    onQuickExit: () -> Unit = {},
    onReportTabSelected: () -> Unit = {},
    onOfficerTabSelected: () -> Unit = {}
) {
    AdminHome(onLogout = onLogout, onQuickExit = onQuickExit)
}
