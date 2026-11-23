package com.example.gbvapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbvapp.data.firebase.AuthRepository
import com.example.gbvapp.data.firebase.CurrentOfficer
import com.example.gbvapp.data.firebase.OfficerAdminRepository
import com.example.gbvapp.data.firebase.OfficerProfile
import com.example.gbvapp.data.firebase.OfficerStatus
import com.example.gbvapp.ui.theme.AppBackground
import com.example.gbvapp.ui.theme.BlueAccent
import com.example.gbvapp.ui.theme.NavyPrimary
import com.example.gbvapp.ui.theme.SurfaceBorder
import com.example.gbvapp.ui.theme.SurfaceCard
import com.example.gbvapp.ui.theme.TextPrimary
import com.example.gbvapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private enum class ApprovalActionType { APPROVE, REJECT, DISABLE }

private data class PendingConfirmAction(
    val officer: OfficerProfile,
    val type: ApprovalActionType
)

@Composable
fun OfficerApprovalsScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var pendingOfficers by remember { mutableStateOf<List<OfficerProfile>?>(null) }
    var approvedOfficers by remember { mutableStateOf<List<OfficerProfile>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf(false) }
    var actionErrorMessage by remember { mutableStateOf<String?>(null) }
    var pendingConfirmAction by remember { mutableStateOf<PendingConfirmAction?>(null) }
    var isActionInProgress by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val currentUid = remember { AuthRepository.getCurrentOfficerId() ?: CurrentOfficer.profile?.uid }

    fun loadData() {
        scope.launch {
            isLoading = true
            loadError = false
            actionErrorMessage = null
            try {
                val pendingRes = OfficerAdminRepository.listByStatus(OfficerStatus.PENDING)
                val approvedRes = OfficerAdminRepository.listByStatus(OfficerStatus.APPROVED)

                if (pendingRes == null || approvedRes == null) {
                    loadError = true
                    pendingOfficers = null
                    approvedOfficers = null
                } else {
                    pendingOfficers = pendingRes
                    approvedOfficers = approvedRes
                }
            } catch (_: Exception) {
                loadError = true
                pendingOfficers = null
                approvedOfficers = null
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NavyPrimary,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Officer Approvals",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceCard,
            contentColor = NavyPrimary,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NavyPrimary
                    )
                }
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Pending",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "Approved",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
        }

        if (actionErrorMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color(0xFFFEE2E2),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Text(
                    text = actionErrorMessage!!,
                    color = Color(0xFF991B1B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NavyPrimary)
                }
            } else if (loadError) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Could not load officers.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { loadData() },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Retry", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                val currentList = if (selectedTab == 0) pendingOfficers else approvedOfficers

                if (currentList == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Could not load officers.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { loadData() },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Retry", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (currentList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedTab == 0) "No pending requests." else "No approved officers.",
                            fontSize = 15.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentList, key = { it.uid }) { officer ->
                            OfficerApprovalCard(
                                officer = officer,
                                isPendingTab = selectedTab == 0,
                                isSelf = officer.uid == currentUid,
                                onApproveClick = {
                                    pendingConfirmAction = PendingConfirmAction(officer, ApprovalActionType.APPROVE)
                                },
                                onRejectClick = {
                                    pendingConfirmAction = PendingConfirmAction(officer, ApprovalActionType.REJECT)
                                },
                                onDisableClick = {
                                    pendingConfirmAction = PendingConfirmAction(officer, ApprovalActionType.DISABLE)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (pendingConfirmAction != null) {
        val action = pendingConfirmAction!!
        val titleText = when (action.type) {
            ApprovalActionType.APPROVE -> "Approve Officer"
            ApprovalActionType.REJECT -> "Reject Officer"
            ApprovalActionType.DISABLE -> "Disable Officer"
        }
        val messageText = when (action.type) {
            ApprovalActionType.APPROVE -> "Are you sure you want to approve ${action.officer.name} (${action.officer.email})?"
            ApprovalActionType.REJECT -> "Are you sure you want to reject ${action.officer.name} (${action.officer.email})?"
            ApprovalActionType.DISABLE -> "Are you sure you want to disable ${action.officer.name} (${action.officer.email})?"
        }
        val confirmBtnText = when (action.type) {
            ApprovalActionType.APPROVE -> "Approve"
            ApprovalActionType.REJECT -> "Reject"
            ApprovalActionType.DISABLE -> "Disable"
        }

        AlertDialog(
            onDismissRequest = {
                if (!isActionInProgress) {
                    pendingConfirmAction = null
                }
            },
            title = {
                Text(text = titleText, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            },
            text = {
                Text(text = messageText, fontSize = 14.sp, color = TextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        isActionInProgress = true
                        scope.launch {
                            try {
                                val success = when (action.type) {
                                    ApprovalActionType.APPROVE -> OfficerAdminRepository.approve(action.officer.uid)
                                    ApprovalActionType.REJECT -> OfficerAdminRepository.reject(action.officer.uid)
                                    ApprovalActionType.DISABLE -> OfficerAdminRepository.disable(action.officer.uid)
                                }
                                if (success) {
                                    pendingConfirmAction = null
                                    actionErrorMessage = null
                                    loadData()
                                } else {
                                    actionErrorMessage = "Could not update the account. Please try again."
                                    pendingConfirmAction = null
                                }
                            } finally {
                                isActionInProgress = false
                            }
                        }
                    },
                    enabled = !isActionInProgress,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (action.type == ApprovalActionType.APPROVE) NavyPrimary else Color(0xFFDC2626)
                    )
                ) {
                    Text(text = confirmBtnText, color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingConfirmAction = null },
                    enabled = !isActionInProgress
                ) {
                    Text(text = "Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun OfficerApprovalCard(
    officer: OfficerProfile,
    isPendingTab: Boolean,
    isSelf: Boolean,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit,
    onDisableClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SurfaceBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = officer.name.ifBlank { "Officer" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = officer.email,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            if (!isSelf) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isPendingTab) {
                        OutlinedButton(
                            onClick = onRejectClick,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFDC2626))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Reject", color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onApproveClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Approve", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onDisableClick,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFDC2626))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonOff,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Disable", color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfficerApprovalsScreenPreview() {
    OfficerApprovalsScreen()
}
