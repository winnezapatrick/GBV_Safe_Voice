package com.example.gbvapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbvapp.data.firebase.AuditLogRecord
import com.example.gbvapp.data.firebase.AuthRepository
import com.example.gbvapp.data.firebase.CaseManagementRepository
import com.example.gbvapp.data.firebase.CaseOperationResult
import com.example.gbvapp.data.firebase.CurrentOfficer
import com.example.gbvapp.data.firebase.OfficerAdminRepository
import com.example.gbvapp.data.firebase.OfficerProfile
import com.example.gbvapp.data.firebase.OfficerStatus
import com.example.gbvapp.data.firebase.ReportDetailModel
import com.example.gbvapp.data.firebase.ReportRepository
import com.example.gbvapp.data.firebase.ReportsResult
import com.example.gbvapp.ui.components.AppBottomBar
import com.example.gbvapp.ui.components.BottomNavItemData
import com.example.gbvapp.ui.components.EmptyState
import com.example.gbvapp.ui.components.ErrorState
import com.example.gbvapp.ui.components.LoadingState
import com.example.gbvapp.ui.components.QuickActionTile
import com.example.gbvapp.ui.components.StatusChip
import com.example.gbvapp.ui.components.SummaryCard
import com.example.gbvapp.ui.theme.AppBackground
import com.example.gbvapp.ui.theme.BlueAccent
import com.example.gbvapp.ui.theme.LightBlueTint
import com.example.gbvapp.ui.theme.NavyPrimary
import com.example.gbvapp.ui.theme.SurfaceBorder
import com.example.gbvapp.ui.theme.SurfaceCard
import com.example.gbvapp.ui.theme.TextPrimary
import com.example.gbvapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private enum class OfficerConfirmType { APPROVE, REJECT, DISABLE, ENABLE }

private data class OfficerConfirmSpec(
    val officer: OfficerProfile,
    val type: OfficerConfirmType
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHome(
    onLogout: () -> Unit = {},
    onQuickExit: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCaseRef by remember { mutableStateOf<String?>(null) }
    var casesFilter by remember { mutableStateOf("All") }
    var officersSubTab by remember { mutableIntStateOf(0) } // 0: Pending, 1: Approved, 2: Disabled
    var showActivityLog by remember { mutableStateOf(false) }

    var reportsList by remember { mutableStateOf<List<ReportDetailModel>>(emptyList()) }
    var pendingOfficersCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Assign Sheet State
    var assigningReport by remember { mutableStateOf<ReportDetailModel?>(null) }
    var approvedOfficersList by remember { mutableStateOf<List<OfficerProfile>>(emptyList()) }
    var selectedOfficerForAssign by remember { mutableStateOf<OfficerProfile?>(null) }
    var isAssigningInProgress by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun loadAdminData() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val reportsRes = ReportRepository.getAllReports()
                val pendingOfficersRes = OfficerAdminRepository.listByStatus(OfficerStatus.PENDING)

                if (reportsRes is ReportsResult.Error) {
                    errorMessage = reportsRes.message
                } else if (reportsRes is ReportsResult.Success) {
                    reportsList = reportsRes.reports
                    pendingOfficersCount = pendingOfficersRes?.size ?: 0
                }
            } catch (_: Exception) {
                errorMessage = "Could not load admin data. Check connection and retry."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedTab) {
        loadAdminData()
    }

    if (showActivityLog) {
        AdminActivityLogScreen(onBack = { showActivityLog = false })
    } else if (selectedCaseRef != null) {
        OfficerCaseDetailsScreen(
            reference = selectedCaseRef!!,
            onBack = {
                selectedCaseRef = null
                loadAdminData()
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
        ) {
            // Top Bar: Screen title only
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NavyPrimary,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    val title = when (selectedTab) {
                        0 -> "Admin Overview"
                        1 -> "All Cases"
                        2 -> "Manage Officers"
                        else -> "Admin Menu"
                    }
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> AdminOverviewTab(
                        reports = reportsList,
                        pendingOfficersCount = pendingOfficersCount,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onRetry = { loadAdminData() },
                        onOpenCasesFilter = { filter ->
                            casesFilter = filter
                            selectedTab = 1
                        },
                        onOpenOfficersTab = { subTab ->
                            officersSubTab = subTab
                            selectedTab = 2
                        },
                        onOpenActivityLog = { showActivityLog = true }
                    )
                    1 -> AdminCasesTab(
                        reports = reportsList,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        initialFilter = casesFilter,
                        onFilterChanged = { casesFilter = it },
                        onRetry = { loadAdminData() },
                        onOpenCase = { ref -> selectedCaseRef = ref },
                        onAssignClick = { report ->
                            scope.launch {
                                val approved = OfficerAdminRepository.listByStatus(OfficerStatus.APPROVED) ?: emptyList()
                                approvedOfficersList = approved
                                selectedOfficerForAssign = approved.firstOrNull()
                                assigningReport = report
                            }
                        }
                    )
                    2 -> AdminOfficersTab(
                        initialSubTab = officersSubTab,
                        onSubTabChanged = { officersSubTab = it }
                    )
                    3 -> AdminMenuTab(
                        onLogout = onLogout,
                        onQuickExit = onQuickExit,
                        onOpenActivityLog = { showActivityLog = true }
                    )
                }
            }

            AppBottomBar(
                items = listOf(
                    BottomNavItemData(
                        label = "Overview",
                        icon = Icons.Default.Dashboard,
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    ),
                    BottomNavItemData(
                        label = "Cases",
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    ),
                    BottomNavItemData(
                        label = "Officers",
                        icon = Icons.Default.People,
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    ),
                    BottomNavItemData(
                        label = "Menu",
                        icon = Icons.Default.Menu,
                        isSelected = selectedTab == 3,
                        onClick = { selectedTab = 3 }
                    )
                )
            )
        }
    }

    // Assign / Reassign Bottom Sheet
    if (assigningReport != null) {
        ModalBottomSheet(
            onDismissRequest = { assigningReport = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Assign Case ${assigningReport!!.reference}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                if (approvedOfficersList.isEmpty()) {
                    Text(
                        text = "No approved officers yet. Approve one in the Officers tab.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                } else {
                    Text(
                        text = "Select an approved officer to assign to this report:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        approvedOfficersList.forEach { officer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedOfficerForAssign = officer }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedOfficerForAssign?.uid == officer.uid,
                                    onClick = { selectedOfficerForAssign = officer },
                                    colors = RadioButtonDefaults.colors(selectedColor = BlueAccent)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(officer.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(officer.email, fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (selectedOfficerForAssign != null) {
                                isAssigningInProgress = true
                                scope.launch {
                                    try {
                                        val res = CaseManagementRepository.assignReport(
                                            reference = assigningReport!!.reference,
                                            officerId = selectedOfficerForAssign!!.uid,
                                            officerName = selectedOfficerForAssign!!.name
                                        )
                                        if (res is CaseOperationResult.Success) {
                                            val assignedName = selectedOfficerForAssign!!.name
                                            assigningReport = null
                                            loadAdminData()
                                            snackbarHostState.showSnackbar("Case assigned to $assignedName.")
                                        } else if (res is CaseOperationResult.Error) {
                                            snackbarHostState.showSnackbar(res.message)
                                        }
                                    } finally {
                                        isAssigningInProgress = false
                                    }
                                }
                            }
                        },
                        enabled = selectedOfficerForAssign != null && !isAssigningInProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text(
                            text = if (isAssigningInProgress) "Assigning..." else "Confirm assignment",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOverviewTab(
    reports: List<ReportDetailModel>,
    pendingOfficersCount: Int,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onOpenCasesFilter: (String) -> Unit,
    onOpenOfficersTab: (Int) -> Unit,
    onOpenActivityLog: () -> Unit
) {
    if (isLoading) {
        LoadingState()
        return
    }
    if (errorMessage != null) {
        ErrorState(message = errorMessage, onRetry = onRetry)
        return
    }

    val unassignedCount = reports.count { it.status == "Pending" }
    val assignedCount = reports.count { it.status == "Assigned" }
    val openCount = reports.count { it.status == "Open" }
    val resolvedCount = reports.count { it.status == "Resolved" }
    val closedCount = reports.count { it.status == "Closed" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "System Administration",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Overview of reports and officer approvals",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        // Tappable Notification Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (unassignedCount > 0) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCasesFilter("Unassigned") },
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCD34D))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$unassignedCount report(s) waiting to be assigned",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            if (pendingOfficersCount > 0) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenOfficersTab(0) },
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = BlueAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$pendingOfficersCount officer(s) waiting for approval",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                }
            }
        }

        // Summary Cards for all reports (5 total: 2 columns, 5th full width)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Unassigned",
                    value = unassignedCount.toString(),
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    onClick = { onOpenCasesFilter("Unassigned") }
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Assigned",
                    value = assignedCount.toString(),
                    icon = Icons.Default.AssignmentInd,
                    onClick = { onOpenCasesFilter("Assigned") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Open",
                    value = openCount.toString(),
                    icon = Icons.Default.FolderOpen,
                    onClick = { onOpenCasesFilter("Open") }
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Resolved",
                    value = resolvedCount.toString(),
                    icon = Icons.Default.CheckCircle,
                    onClick = { onOpenCasesFilter("Resolved") }
                )
            }

            SummaryCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Closed",
                value = closedCount.toString(),
                icon = Icons.Default.Schedule,
                onClick = { onOpenCasesFilter("Closed") }
            )
        }

        // Quick Actions
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { onOpenCasesFilter("Unassigned") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AssignmentInd,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Assign reports",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionTile(
                    modifier = Modifier.weight(1f),
                    title = "Approve officers",
                    icon = Icons.Default.People,
                    onClick = { onOpenOfficersTab(0) }
                )
                QuickActionTile(
                    modifier = Modifier.weight(1f),
                    title = "All cases",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    onClick = { onOpenCasesFilter("All") }
                )
                QuickActionTile(
                    modifier = Modifier.weight(1f),
                    title = "Activity log",
                    icon = Icons.Default.History,
                    onClick = onOpenActivityLog
                )
            }
        }
    }
}

@Composable
private fun AdminCasesTab(
    reports: List<ReportDetailModel>,
    isLoading: Boolean,
    errorMessage: String?,
    initialFilter: String,
    onFilterChanged: (String) -> Unit,
    onRetry: () -> Unit,
    onOpenCase: (String) -> Unit,
    onAssignClick: (ReportDetailModel) -> Unit
) {
    if (isLoading) {
        LoadingState()
        return
    }
    if (errorMessage != null) {
        ErrorState(message = errorMessage, onRetry = onRetry)
        return
    }

    val filterOptions = listOf("All", "Unassigned", "Assigned", "Open", "Resolved", "Closed")

    val filteredReports = when (initialFilter) {
        "All" -> reports
        "Unassigned" -> reports.filter { it.status == "Pending" }
        else -> reports.filter { it.status.equals(initialFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    FilterChip(
                        selected = initialFilter == filter,
                        onClick = { onFilterChanged(filter) },
                        label = { Text(filter, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            IconButton(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = NavyPrimary
                )
            }
        }

        if (filteredReports.isEmpty()) {
            EmptyState(message = "No cases here yet.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredReports, key = { it.reference }) { report ->
                    AdminCaseCard(
                        report = report,
                        onOpenCase = { onOpenCase(report.reference) },
                        onAssignClick = { onAssignClick(report) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminCaseCard(
    report: ReportDetailModel,
    onOpenCase: () -> Unit,
    onAssignClick: () -> Unit
) {
    val dateText = ReportRepository.formatTimestamp(report.submissionTimestamp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenCase),
        color = SurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SurfaceBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.reference,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = report.category,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                StatusChip(status = report.status)
            }

            Text(
                text = "Assigned Officer: ${report.assignedOfficerName ?: "Not assigned"}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reported: $dateText",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                val buttonText = when (report.status) {
                    "Pending" -> "Assign"
                    "Assigned" -> "Reassign"
                    else -> "View"
                }

                Button(
                    onClick = {
                        if (report.status == "Pending" || report.status == "Assigned") {
                            onAssignClick()
                        } else {
                            onOpenCase()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (report.status == "Pending" || report.status == "Assigned") NavyPrimary else BlueAccent
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(buttonText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun AdminOfficersTab(
    initialSubTab: Int,
    onSubTabChanged: (Int) -> Unit
) {
    var pendingOfficers by remember { mutableStateOf<List<OfficerProfile>?>(null) }
    var approvedOfficers by remember { mutableStateOf<List<OfficerProfile>?>(null) }
    var disabledOfficers by remember { mutableStateOf<List<OfficerProfile>?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf(false) }
    var actionErrorMessage by remember { mutableStateOf<String?>(null) }
    var pendingConfirmSpec by remember { mutableStateOf<OfficerConfirmSpec?>(null) }
    var isActionInProgress by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val currentUid = remember { AuthRepository.getCurrentOfficerId() ?: CurrentOfficer.profile?.uid }

    fun loadOfficers() {
        scope.launch {
            isLoading = true
            loadError = false
            actionErrorMessage = null
            try {
                val pendingRes = OfficerAdminRepository.listByStatus(OfficerStatus.PENDING)
                val approvedRes = OfficerAdminRepository.listByStatus(OfficerStatus.APPROVED)
                val disabledRes = OfficerAdminRepository.listByStatus(OfficerStatus.DISABLED)

                if (pendingRes == null || approvedRes == null || disabledRes == null) {
                    loadError = true
                } else {
                    pendingOfficers = pendingRes
                    approvedOfficers = approvedRes
                    disabledOfficers = disabledRes
                }
            } catch (_: Exception) {
                loadError = true
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadOfficers()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = initialSubTab,
            containerColor = SurfaceCard,
            contentColor = NavyPrimary,
            indicator = { tabPositions ->
                if (initialSubTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[initialSubTab]),
                        color = NavyPrimary
                    )
                }
            }
        ) {
            Tab(selected = initialSubTab == 0, onClick = { onSubTabChanged(0) }) {
                Text("Pending", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = initialSubTab == 1, onClick = { onSubTabChanged(1) }) {
                Text("Approved", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = initialSubTab == 2, onClick = { onSubTabChanged(2) }) {
                Text("Disabled", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (isLoading) {
                LoadingState()
            } else if (loadError) {
                ErrorState(message = "Could not load officers.", onRetry = { loadOfficers() })
            } else {
                val currentList = when (initialSubTab) {
                    0 -> pendingOfficers
                    1 -> approvedOfficers
                    else -> disabledOfficers
                }

                if (currentList == null) {
                    ErrorState(message = "Could not load officers.", onRetry = { loadOfficers() })
                } else if (currentList.isEmpty()) {
                    EmptyState(
                        message = when (initialSubTab) {
                            0 -> "No pending requests."
                            1 -> "No approved officers."
                            else -> "No disabled officers."
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentList, key = { it.uid }) { officer ->
                            AdminOfficerRowCard(
                                officer = officer,
                                subTab = initialSubTab,
                                isSelf = officer.uid == currentUid,
                                onApprove = { pendingConfirmSpec = OfficerConfirmSpec(officer, OfficerConfirmType.APPROVE) },
                                onReject = { pendingConfirmSpec = OfficerConfirmSpec(officer, OfficerConfirmType.REJECT) },
                                onDisable = { pendingConfirmSpec = OfficerConfirmSpec(officer, OfficerConfirmType.DISABLE) },
                                onEnable = { pendingConfirmSpec = OfficerConfirmSpec(officer, OfficerConfirmType.ENABLE) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (pendingConfirmSpec != null) {
        val spec = pendingConfirmSpec!!
        val actionTitle = when (spec.type) {
            OfficerConfirmType.APPROVE -> "Approve Officer"
            OfficerConfirmType.REJECT -> "Reject Officer"
            OfficerConfirmType.DISABLE -> "Disable Officer"
            OfficerConfirmType.ENABLE -> "Enable Officer"
        }
        val actionMessage = "Are you sure you want to ${spec.type.name.lowercase()} ${spec.officer.name} (${spec.officer.email})?"

        AlertDialog(
            onDismissRequest = { if (!isActionInProgress) pendingConfirmSpec = null },
            title = { Text(actionTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = { Text(actionMessage, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        isActionInProgress = true
                        actionErrorMessage = null
                        scope.launch {
                            try {
                                val success = when (spec.type) {
                                    OfficerConfirmType.APPROVE -> OfficerAdminRepository.approve(spec.officer.uid)
                                    OfficerConfirmType.REJECT -> OfficerAdminRepository.reject(spec.officer.uid)
                                    OfficerConfirmType.DISABLE -> OfficerAdminRepository.disable(spec.officer.uid)
                                    OfficerConfirmType.ENABLE -> OfficerAdminRepository.approve(spec.officer.uid)
                                }
                                if (success) {
                                    pendingConfirmSpec = null
                                    loadOfficers()
                                } else {
                                    actionErrorMessage = "Could not update the account. Please try again."
                                    pendingConfirmSpec = null
                                }
                            } finally {
                                isActionInProgress = false
                            }
                        }
                    },
                    enabled = !isActionInProgress,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (spec.type == OfficerConfirmType.APPROVE || spec.type == OfficerConfirmType.ENABLE) NavyPrimary else Color(0xFFDC2626)
                    )
                ) {
                    Text(spec.type.name, color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingConfirmSpec = null }, enabled = !isActionInProgress) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun AdminOfficerRowCard(
    officer: OfficerProfile,
    subTab: Int,
    isSelf: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDisable: () -> Unit,
    onEnable: () -> Unit
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
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(officer.name.ifBlank { "Officer" }, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(officer.email, fontSize = 13.sp, color = TextSecondary)
                }
            }

            if (!isSelf) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (subTab) {
                        0 -> {
                            OutlinedButton(onClick = onReject, border = BorderStroke(1.dp, Color(0xFFDC2626))) {
                                Text("Reject", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = onApprove, colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)) {
                                Text("Approve", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        1 -> {
                            OutlinedButton(onClick = onDisable, border = BorderStroke(1.dp, Color(0xFFDC2626))) {
                                Text("Disable", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                            }
                        }
                        2 -> {
                            Button(onClick = onEnable, colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)) {
                                Text("Enable", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminActivityLogScreen(
    onBack: () -> Unit
) {
    var logsList by remember { mutableStateOf<List<AuditLogRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun loadLogs() {
        scope.launch {
            isLoading = true
            isError = false
            try {
                logsList = CaseManagementRepository.getAuditLogs()
            } catch (_: Exception) {
                isError = true
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadLogs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
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
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Activity Log", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (isLoading) {
                LoadingState()
            } else if (isError) {
                ErrorState(message = "Could not load activity log.", onRetry = { loadLogs() })
            } else if (logsList.isEmpty()) {
                EmptyState(message = "No audit log entries recorded yet.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(logsList, key = { it.id }) { log ->
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SurfaceBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(log.action, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    Text(ReportRepository.formatTimestamp(log.timestamp), fontSize = 11.sp, color = TextSecondary)
                                }
                                Text(log.details, fontSize = 13.sp, color = TextPrimary)
                                Text("Target: ${log.targetId.ifBlank { "N/A" }} • By: ${log.performedBy}", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMenuTab(
    onLogout: () -> Unit,
    onQuickExit: () -> Unit,
    onOpenActivityLog: () -> Unit
) {
    val profile = CurrentOfficer.profile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            color = SurfaceCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SurfaceBorder)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(NavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(profile?.name ?: "Administrator", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(profile?.email ?: "", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(color = LightBlueTint, shape = RoundedCornerShape(12.dp)) {
                        Text("ADMINISTRATOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
            }
        }

        Button(
            onClick = onOpenActivityLog,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
            Text("Activity log", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        OutlinedButton(
            onClick = onQuickExit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, NavyPrimary)
        ) {
            Text("Quick exit", color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Button(
            onClick = {
                AuthRepository.logout()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log out", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
