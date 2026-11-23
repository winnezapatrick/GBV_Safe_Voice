package com.example.gbvapp.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.gbvapp.data.firebase.CaseManagementRepository
import com.example.gbvapp.data.firebase.CaseNote
import com.example.gbvapp.data.firebase.CaseOperationResult
import com.example.gbvapp.data.firebase.CaseReferral
import com.example.gbvapp.data.firebase.CaseUpdateRecord
import com.example.gbvapp.data.firebase.CurrentOfficer
import com.example.gbvapp.data.firebase.ReportDetailModel
import com.example.gbvapp.data.firebase.ReportRepository
import com.example.gbvapp.data.firebase.SingleReportResult
import com.example.gbvapp.ui.components.ErrorState
import com.example.gbvapp.ui.components.LoadingState
import com.example.gbvapp.ui.components.StatusChip
import com.example.gbvapp.ui.theme.AppBackground
import com.example.gbvapp.ui.theme.BlueAccent
import com.example.gbvapp.ui.theme.LightBlueTint
import com.example.gbvapp.ui.theme.NavyPrimary
import com.example.gbvapp.ui.theme.SurfaceBorder
import com.example.gbvapp.ui.theme.SurfaceCard
import com.example.gbvapp.ui.theme.TextPrimary
import com.example.gbvapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun OfficerCaseDetailsScreen(
    reference: String,
    openReferralsTab: Boolean = false,
    onBack: () -> Unit
) {
    var report by remember { mutableStateOf<ReportDetailModel?>(null) }
    var notesList by remember { mutableStateOf<List<CaseNote>>(emptyList()) }
    var referralsList by remember { mutableStateOf<List<CaseReferral>>(emptyList()) }
    var historyList by remember { mutableStateOf<List<CaseUpdateRecord>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var actionErrorMessage by remember { mutableStateOf<String?>(null) }
    var isActionInProgress by remember { mutableStateOf(false) }

    var showCloseConfirmDialog by remember { mutableStateOf(false) }
    var enlargedPhotoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Note form state
    var isAddingNote by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }

    // Referral form state
    var isAddingReferral by remember { mutableStateOf(openReferralsTab) }
    var referralType by remember { mutableStateOf("Counseling") }
    var targetDept by remember { mutableStateOf("Student Affairs") }
    var referralNotes by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val officerName = CurrentOfficer.profile?.name ?: "Officer"

    fun loadData() {
        scope.launch {
            isLoading = true
            errorMessage = null
            when (val res = ReportRepository.getReport(reference)) {
                is SingleReportResult.Success -> {
                    report = res.report
                    notesList = CaseManagementRepository.getCaseNotes(reference)
                    referralsList = CaseManagementRepository.getReferrals(reference)
                    historyList = CaseManagementRepository.getCaseUpdates(reference)
                    isLoading = false
                }
                is SingleReportResult.NotFound -> {
                    errorMessage = "Case not found."
                    isLoading = false
                }
                is SingleReportResult.Error -> {
                    errorMessage = res.message
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(reference) {
        loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Top Header with Back Arrow
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
                    text = "Case Details",
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
            if (isLoading) {
                LoadingState()
            } else if (errorMessage != null) {
                ErrorState(message = errorMessage!!, onRetry = { loadData() })
            } else if (report != null) {
                val currentReport = report!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (actionErrorMessage != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(10.dp),
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

                    // 1. Summary Section
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentReport.reference,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                StatusChip(status = currentReport.status)
                            }

                            Text(
                                text = currentReport.category,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            DetailRow("Submitted Date", ReportRepository.formatTimestamp(currentReport.submissionTimestamp))
                            DetailRow("Assigned Officer", currentReport.assignedOfficerName ?: "Unassigned")
                            if (currentReport.status == "Closed") {
                                DetailRow("Closure Date", ReportRepository.formatTimestamp(currentReport.closureDate))
                            }
                        }
                    }

                    // 2. Description Section
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Description",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text(
                                text = currentReport.description,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // 3. Incident Details Section
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
                                text = "Incident Details",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            DetailRow("Incident Date", currentReport.incidentDate ?: "Not provided")
                            DetailRow("Incident Time", currentReport.incidentTime ?: "Not provided")
                            DetailRow("Location", currentReport.location ?: "Not provided")
                        }
                    }

                    // 4. Evidence Section
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
                                text = "Evidence",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )

                            if (currentReport.evidencePaths.isEmpty()) {
                                Text(
                                    text = "No evidence submitted.",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    itemsIndexed(currentReport.evidencePaths) { index, path ->
                                        EvidenceThumbnailItem(
                                            index = index + 1,
                                            path = path,
                                            onImageClick = { bitmap -> enlargedPhotoBitmap = bitmap }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. Actions Section
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Actions",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )

                            when (currentReport.status) {
                                "Assigned" -> {
                                    Button(
                                        onClick = {
                                            isActionInProgress = true
                                            actionErrorMessage = null
                                            scope.launch {
                                                try {
                                                    val res = CaseManagementRepository.updateCaseStatus(reference, "Open")
                                                    if (res is CaseOperationResult.Success) {
                                                        loadData()
                                                    } else if (res is CaseOperationResult.Error) {
                                                        actionErrorMessage = res.message
                                                    }
                                                } finally {
                                                    isActionInProgress = false
                                                }
                                            }
                                        },
                                        enabled = !isActionInProgress,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                                    ) {
                                        Text("Start review", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                                "Open" -> {
                                    Button(
                                        onClick = {
                                            isActionInProgress = true
                                            actionErrorMessage = null
                                            scope.launch {
                                                try {
                                                    val res = CaseManagementRepository.updateCaseStatus(reference, "Resolved")
                                                    if (res is CaseOperationResult.Success) {
                                                        loadData()
                                                    } else if (res is CaseOperationResult.Error) {
                                                        actionErrorMessage = res.message
                                                    }
                                                } finally {
                                                    isActionInProgress = false
                                                }
                                            }
                                        },
                                        enabled = !isActionInProgress,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                                    ) {
                                        Text("Mark as resolved", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    OutlinedButton(
                                        onClick = { showCloseConfirmDialog = true },
                                        enabled = !isActionInProgress,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFDC2626))
                                    ) {
                                        Text("Close case", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                    }
                                }
                                "Resolved" -> {
                                    Button(
                                        onClick = { showCloseConfirmDialog = true },
                                        enabled = !isActionInProgress,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                                    ) {
                                        Text("Close case", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                                "Closed" -> {
                                    Text(
                                        text = "This case is closed.",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // 6. Notes Section
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Notes",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                if (currentReport.status != "Closed") {
                                    TextButton(onClick = { isAddingNote = !isAddingNote }) {
                                        Text(if (isAddingNote) "Cancel" else "+ Add Note", fontWeight = FontWeight.Bold, color = BlueAccent)
                                    }
                                }
                            }

                            if (isAddingNote) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = noteText,
                                        onValueChange = { noteText = it },
                                        placeholder = { Text("Enter confidential case note...", fontSize = 13.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BlueAccent,
                                            unfocusedBorderColor = SurfaceBorder,
                                            focusedContainerColor = LightBlueTint,
                                            unfocusedContainerColor = LightBlueTint
                                        )
                                    )
                                    Button(
                                        onClick = {
                                            if (noteText.isNotBlank()) {
                                                scope.launch {
                                                    val success = CaseManagementRepository.addCaseNote(reference, noteText, officerName)
                                                    if (success) {
                                                        noteText = ""
                                                        isAddingNote = false
                                                        notesList = CaseManagementRepository.getCaseNotes(reference)
                                                    }
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                                    ) {
                                        Text("Save Note", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (notesList.isEmpty()) {
                                Text("No notes added yet.", fontSize = 13.sp, color = TextSecondary)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    notesList.forEach { note ->
                                        Surface(
                                            color = LightBlueTint,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(note.noteText, fontSize = 13.sp, color = TextPrimary)
                                                Text("By ${note.authorName} • ${ReportRepository.formatTimestamp(note.timestamp)}", fontSize = 11.sp, color = TextSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 7. Referrals Section
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Referrals",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                if (currentReport.status != "Closed") {
                                    TextButton(onClick = { isAddingReferral = !isAddingReferral }) {
                                        Text(if (isAddingReferral) "Cancel" else "+ Add Referral", fontWeight = FontWeight.Bold, color = BlueAccent)
                                    }
                                }
                            }

                            if (isAddingReferral) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = referralType,
                                        onValueChange = { referralType = it },
                                        label = { Text("Referral Type (e.g. Counseling, Healthcare)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    OutlinedTextField(
                                        value = targetDept,
                                        onValueChange = { targetDept = it },
                                        label = { Text("Target Department") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    OutlinedTextField(
                                        value = referralNotes,
                                        onValueChange = { referralNotes = it },
                                        label = { Text("Referral Notes (optional)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    Button(
                                        onClick = {
                                            if (referralType.isNotBlank() && targetDept.isNotBlank()) {
                                                scope.launch {
                                                    val success = CaseManagementRepository.addReferral(reference, referralType, targetDept, referralNotes, officerName)
                                                    if (success) {
                                                        referralNotes = ""
                                                        isAddingReferral = false
                                                        referralsList = CaseManagementRepository.getReferrals(reference)
                                                    }
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                                    ) {
                                        Text("Record Referral", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (referralsList.isEmpty()) {
                                Text("No referrals recorded yet.", fontSize = 13.sp, color = TextSecondary)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    referralsList.forEach { referral ->
                                        Surface(
                                            color = Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text("${referral.referralType} -> ${referral.targetDepartment}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                if (referral.notes.isNotBlank()) {
                                                    Text(referral.notes, fontSize = 12.sp, color = TextSecondary)
                                                }
                                                Text("Recorded by ${referral.officerName} • ${ReportRepository.formatTimestamp(referral.timestamp)}", fontSize = 11.sp, color = TextSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 8. History Timeline Section
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
                                text = "History Timeline",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )

                            if (historyList.isEmpty()) {
                                Text("No history events logged.", fontSize = 13.sp, color = TextSecondary)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    historyList.forEach { update ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            StatusChip(status = update.status)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = if (update.type == "assignment") "Assigned to officer" else "Status updated to ${update.status}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = ReportRepository.formatTimestamp(update.timestamp),
                                                    fontSize = 11.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState)
    }

    // Confirmation Dialog for Close Case
    if (showCloseConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isActionInProgress) showCloseConfirmDialog = false
            },
            title = {
                Text("Close Case", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            },
            text = {
                Text("Are you sure you want to close this case?", fontSize = 14.sp, color = TextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        isActionInProgress = true
                        actionErrorMessage = null
                        scope.launch {
                            try {
                                val res = CaseManagementRepository.updateCaseStatus(reference, "Closed")
                                if (res is CaseOperationResult.Success) {
                                    showCloseConfirmDialog = false
                                    loadData()
                                    snackbarHostState.showSnackbar("Case closed.")
                                } else if (res is CaseOperationResult.Error) {
                                    actionErrorMessage = "Could not update the case. Please try again."
                                    showCloseConfirmDialog = false
                                }
                            } finally {
                                isActionInProgress = false
                            }
                        }
                    },
                    enabled = !isActionInProgress,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Confirm Close", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCloseConfirmDialog = false },
                    enabled = !isActionInProgress
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Enlarged Photo Dialog
    if (enlargedPhotoBitmap != null) {
        AlertDialog(
            onDismissRequest = { enlargedPhotoBitmap = null },
            title = null,
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = enlargedPhotoBitmap!!.asImageBitmap(),
                        contentDescription = "Enlarged evidence",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { enlargedPhotoBitmap = null }) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun EvidenceThumbnailItem(
    index: Int,
    path: String,
    onImageClick: (Bitmap) -> Unit
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(path) {
        isLoading = true
        isError = false
        val loaded = ReportRepository.loadEvidenceBitmap(path)
        if (loaded != null) {
            bitmap = loaded
        } else {
            isError = true
        }
        isLoading = false
    }

    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.05f))
            .clickable(enabled = bitmap != null) { bitmap?.let(onImageClick) },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NavyPrimary)
        } else if (isError || bitmap == null) {
            Text("Failed to load", fontSize = 10.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
        } else {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Evidence $index",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}
