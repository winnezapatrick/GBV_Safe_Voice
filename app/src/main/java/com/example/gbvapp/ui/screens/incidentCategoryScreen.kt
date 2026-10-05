package com.example.gbvapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbvapp.ui.theme.AppBackground
import com.example.gbvapp.ui.theme.BlueAccent
import com.example.gbvapp.ui.theme.CardBorderBlue
import com.example.gbvapp.ui.theme.LightBlueTint
import com.example.gbvapp.ui.theme.NavyPrimary
import com.example.gbvapp.ui.theme.QuickExitBg
import com.example.gbvapp.ui.theme.QuickExitBorder
import com.example.gbvapp.ui.theme.QuickExitRed
import com.example.gbvapp.ui.theme.SurfaceBorder
import com.example.gbvapp.ui.theme.SurfaceCard
import com.example.gbvapp.ui.theme.TextPrimary
import com.example.gbvapp.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IncidentCategoryScreen(
    onBack: () -> Unit = {},
    onSubmitReport: () -> Unit = {},
    onQuickExit: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("Sexual Harassment") }
    var incidentDescription by remember { mutableStateOf("") }

    val attachedFiles = remember {
        mutableStateListOf(
            "photo_evidence_01.jpg" to "1.2 MB • Encrypted"
        )
    }

    val categories = listOf(
        "Sexual Harassment",
        "Sexual Abuse",
        "Physical Abuse",
        "Child Abuse",
        "Domestic Violence",
        "Emotional / Psychological",
        "Other"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Top Header Bar
        TopHeaderBar(
            onBack = onBack,
            onQuickExit = onQuickExit
        )

        // Scrollable Content Form
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Step Progress Bar Indicator
            StepProgressHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // Incident Category Selection
            IncidentCategorySection(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Incident Description Input
            IncidentDescriptionSection(
                description = incidentDescription,
                onDescriptionChange = { incidentDescription = it },
                onClear = { incidentDescription = "" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Evidence Section
            AddEvidenceSection(
                attachedFiles = attachedFiles,
                onRemoveFile = { attachedFiles.removeAt(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = onSubmitReport,
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
                    Text(
                        text = "SUBMIT REPORT",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Single Trust Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Safe submission channel • ISO 27001 standard",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun TopHeaderBar(
    onBack: () -> Unit,
    onQuickExit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Box(
                        modifier = Modifier.padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SafeVoiceLogoIcon(modifier = Modifier.fillMaxSize())
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "SAFE VOICE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Incident Details",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onQuickExit,
                    colors = ButtonDefaults.buttonColors(containerColor = QuickExitBg),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, QuickExitBorder),
                    elevation = null,
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = QuickExitRed,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "QUICK EXIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuickExitRed
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(NavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepProgressHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STEP 2 OF 2",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueAccent,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Incident Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Text(
                text = "Complete",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { 1.0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = NavyPrimary,
            trackColor = SurfaceBorder
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IncidentCategorySection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Incident Category",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Select primary type",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory

                Surface(
                    modifier = Modifier.clickable { onCategorySelected(category) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) NavyPrimary else LightBlueTint,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) NavyPrimary else CardBorderBlue
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IncidentDescriptionSection(
    description: String,
    onDescriptionChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Incident Description *",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "${description.length} / 2000",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 2000) onDescriptionChange(it) },
                placeholder = {
                    Text(
                        text = "Describe the incident in your own words...",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueAccent,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedContainerColor = LightBlueTint,
                    unfocusedContainerColor = AppBackground
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Auto-saved locally",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                TextButton(
                    onClick = onClear,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Clear",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun AddEvidenceSection(
    attachedFiles: List<Pair<String, String>>,
    onRemoveFile: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Add Evidence (Optional)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Upload Area Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = LightBlueTint,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CardBorderBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        tint = NavyPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Upload Supporting Files",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Attach photos, audio, or documents. Max 25MB.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Attached Files List
        attachedFiles.forEachIndexed { index, (fileName, fileInfo) ->
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceCard,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightBlueTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fileName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = fileInfo,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    IconButton(
                        onClick = { onRemoveFile(index) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IncidentCategoryScreenPreview() {
    IncidentCategoryScreen()
}
