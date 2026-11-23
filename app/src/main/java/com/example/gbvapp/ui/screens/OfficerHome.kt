package com.example.gbvapp.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbvapp.data.firebase.AuthRepository
import com.example.gbvapp.data.firebase.CaseManagementRepository
import com.example.gbvapp.data.firebase.CaseNote
import com.example.gbvapp.data.firebase.CaseOperationResult
import com.example.gbvapp.data.firebase.CaseReferral
import com.example.gbvapp.data.firebase.CaseUpdateRecord
import com.example.gbvapp.data.firebase.CurrentOfficer
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
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

@Composable
fun OfficerHome(
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCaseRef by remember { mutableStateOf<String?>(null) }
    var casesFilter by remember { mutableStateOf("All") }
    var referralsOpenDirectly by remember { mutableStateOf(false) }

    var reportsList by remember { mutableStateOf<List<ReportDetailModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val officerId = CurrentOfficer.profile?.uid ?: ""

    fun loadReports() {
        if (officerId.isBlank()) {
            isLoading = false
            errorMessage = "Officer session not found. Please log in again."
            return
        }
        scope.launch {
            isLoading = true
            errorMessage = null
            when (val result = ReportRepository.getAssignedReportsForOfficer(officerId)) {
                is ReportsResult.Success -> {
                    reportsList = result.reports
                    isLoading = false
                }
                is ReportsResult.Error -> {
                    errorMessage = result.message
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(selectedTab) {
        loadReports()
    }

    if (selectedCaseRef != null) {
        OfficerCaseDetailsScreen(
            reference = selectedCaseRef!!,
            openReferralsTab = referralsOpenDirectly,
            onBack = {
                selectedCaseRef = null
                referralsOpenDirectly = false
                loadReports()
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
                        0 -> "Officer Overview"
                        1 -> "Assigned Cases"
                        2 -> "Officer Resources"
                        else -> "Officer Menu"
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
                    0 -> OfficerOverviewTab(
                        reports = reportsList,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onRetry = { loadReports() },
                        onOpenCase = { ref -> selectedCaseRef = ref },
                        onOpenCasesFilter = { filter ->
                            casesFilter = filter
                            selectedTab = 1
                        },
                        onOpenReferrals = {
                            val activeCase = reportsList.firstOrNull { it.status == "Open" || it.status == "Assigned" }
                            if (activeCase != null) {
                                referralsOpenDirectly = true
                                selectedCaseRef = activeCase.reference
                            } else if (reportsList.isNotEmpty()) {
                                selectedCaseRef = reportsList.first().reference
                            } else {
                                casesFilter = "All"
                                selectedTab = 1
                            }
                        },
                        onOpenResources = { selectedTab = 2 }
                    )
                    1 -> OfficerCasesTab(
                        reports = reportsList,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        initialFilter = casesFilter,
                        onFilterChanged = { casesFilter = it },
                        onRetry = { loadReports() },
                        onOpenCase = { ref -> selectedCaseRef = ref }
                    )
                    2 -> OfficerResourcesTab()
                    3 -> OfficerMenuTab(onLogout = onLogout)
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
                        label = "Resources",
                        icon = Icons.Default.Book,
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
}

@Composable
private fun OfficerOverviewTab(
    reports: List<ReportDetailModel>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onOpenCase: (String) -> Unit,
    onOpenCasesFilter: (String) -> Unit,
    onOpenReferrals: () -> Unit,
    onOpenResources: () -> Unit
) {
    if (isLoading) {
        LoadingState()
        return
    }
    if (errorMessage != null) {
        ErrorState(message = errorMessage, onRetry = onRetry)
        return
    }

    val officerName = CurrentOfficer.profile?.name ?: "Officer"
    val assignedCount = reports.count { it.status == "Assigned" }
    val openCount = reports.count { it.status == "Open" }
    val resolvedCount = reports.count { it.status == "Resolved" }
    val closedCount = reports.count { it.status == "Closed" }

    val oldestAssignedCase = reports.filter { it.status == "Assigned" }
        .minByOrNull { (it.submissionTimestamp as? Timestamp)?.seconds ?: Long.MAX_VALUE }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hello Officer header
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Hello, $officerName",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Field Investigation & Case Management",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        // Notification Cards Area
        if (assignedCount == 0 && resolvedCount == 0) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceCard,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "You're all caught up.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (assignedCount > 0) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenCasesFilter("Assigned") },
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = BlueAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "$assignedCount new case(s) assigned to you",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        }
                    }
                }

                if (resolvedCount > 0) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenCasesFilter("Resolved") },
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "$resolvedCount resolved case(s) ready to close",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
            }
        }

        // 2x2 Summary Cards Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Assigned",
                    value = assignedCount.toString(),
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    onClick = { onOpenCasesFilter("Assigned") }
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Open",
                    value = openCount.toString(),
                    icon = Icons.Default.FolderOpen,
                    onClick = { onOpenCasesFilter("Open") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Resolved",
                    value = resolvedCount.toString(),
                    icon = Icons.Default.CheckCircle,
                    onClick = { onOpenCasesFilter("Resolved") }
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Closed",
                    value = closedCount.toString(),
                    icon = Icons.Default.Schedule,
                    onClick = { onOpenCasesFilter("Closed") }
                )
            }
        }

        // Primary Action: Start Next Case
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(
                onClick = {
                    if (oldestAssignedCase != null) {
                        onOpenCase(oldestAssignedCase.reference)
                    }
                },
                enabled = oldestAssignedCase != null,
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
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start next case",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            if (oldestAssignedCase == null) {
                Text(
                    text = "No assigned cases waiting for review.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // 2x2 Secondary Quick Action Tiles
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionTile(
                    modifier = Modifier.weight(1f),
                    title = "My cases",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    onClick = { onOpenCasesFilter("All") }
                )
                QuickActionTile(
                    modifier = Modifier.weight(1f),
                    title = "Ready to close",
                    icon = Icons.Default.CheckCircle,
                    onClick = { onOpenCasesFilter("Resolved") }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionTile(
                    modifier = Modifier.weight(1f),
                    title = "Referrals",
                    icon = Icons.Default.Share,
                    onClick = onOpenReferrals
                )
                QuickActionTile(
                    modifier = Modifier.weight(1f),
                    title = "Resources",
                    icon = Icons.Default.Book,
                    onClick = onOpenResources
                )
            }
        }

        // Recent Cases Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Recent cases",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            if (reports.isEmpty()) {
                EmptyState(message = "No cases assigned to you yet.")
            } else {
                reports.take(3).forEach { report ->
                    OfficerCaseItemCard(report = report, onOpenCase = { onOpenCase(report.reference) })
                }
            }
        }
    }
}

@Composable
private fun OfficerCasesTab(
    reports: List<ReportDetailModel>,
    isLoading: Boolean,
    errorMessage: String?,
    initialFilter: String,
    onFilterChanged: (String) -> Unit,
    onRetry: () -> Unit,
    onOpenCase: (String) -> Unit
) {
    if (isLoading) {
        LoadingState()
        return
    }
    if (errorMessage != null) {
        ErrorState(message = errorMessage, onRetry = onRetry)
        return
    }

    val filterOptions = listOf("All", "Assigned", "Open", "Resolved", "Closed")

    val filteredReports = when (initialFilter) {
        "All" -> reports
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
                    OfficerCaseItemCard(report = report, onOpenCase = { onOpenCase(report.reference) })
                }
            }
        }
    }
}

@Composable
private fun OfficerCaseItemCard(
    report: ReportDetailModel,
    onOpenCase: () -> Unit
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

                Button(
                    onClick = onOpenCase,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Open Case", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun OfficerResourcesTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card 1: How cases move
        Surface(
            color = SurfaceCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "How cases move",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                StatusExplanationRow("Pending", "Report submitted by victim; awaiting admin assignment.")
                StatusExplanationRow("Assigned", "Admin assigned case to officer; officer review pending.")
                StatusExplanationRow("Open", "Officer started formal review and investigation.")
                StatusExplanationRow("Resolved", "Investigation complete; action items fulfilled.")
                StatusExplanationRow("Closed", "Case formally closed and archived.")

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Close a case only when it is ready for formal closure under your organization's procedures.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }

        // Card 2: Handling sensitive information
        Surface(
            color = SurfaceCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Handling sensitive information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                BulletPointText("Do not share or screenshot case details.")
                BulletPointText("Do not discuss cases in public or unsecure channels.")
                BulletPointText("Log out when finished using your device.")
            }
        }

        // Card 3: Support contacts
        Surface(
            color = SurfaceCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Support contacts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                Text(
                    text = "Your organization has not added verified contacts yet.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun StatusExplanationRow(status: String, meaning: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusChip(status = status)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = meaning,
            fontSize = 12.sp,
            color = TextPrimary
        )
    }
}

@Composable
private fun BulletPointText(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text("• ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
        Text(text, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)
    }
}

@Composable
private fun OfficerMenuTab(onLogout: () -> Unit) {
    val profile = CurrentOfficer.profile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Profile Card
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
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile?.name ?: "Officer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = profile?.email ?: "",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = LightBlueTint,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = (profile?.role?.value ?: "officer").uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                AuthRepository.logout()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log out",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
