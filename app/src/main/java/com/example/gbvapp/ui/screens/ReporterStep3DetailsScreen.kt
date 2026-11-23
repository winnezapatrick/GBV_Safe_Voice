package com.example.gbvapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbvapp.ui.components.ReporterHeader
import com.example.gbvapp.ui.theme.AppBackground
import com.example.gbvapp.ui.theme.BlueAccent
import com.example.gbvapp.ui.theme.LightBlueTint
import com.example.gbvapp.ui.theme.NavyPrimary
import com.example.gbvapp.ui.theme.SurfaceBorder
import com.example.gbvapp.ui.theme.TextPrimary
import com.example.gbvapp.ui.theme.TextSecondary

@Composable
fun ReporterStep3DetailsScreen(
    initialDate: String,
    initialTime: String,
    initialLocation: String,
    onBack: () -> Unit,
    onNext: (date: String, time: String, location: String) -> Unit,
    onQuickExit: () -> Unit
) {
    var incidentDate by remember { mutableStateOf(initialDate) }
    var incidentTime by remember { mutableStateOf(initialTime) }
    var location by remember { mutableStateOf(initialLocation) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        ReporterHeader(
            stepNumber = 3,
            title = "Time & Location",
            onBack = onBack,
            onQuickExit = onQuickExit
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Provide details about when and where the incident took place. All fields on this page are optional.",
                fontSize = 14.sp,
                color = TextSecondary
            )

            // Date
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Incident Date (optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = incidentDate,
                    onValueChange = { incidentDate = it },
                    placeholder = { Text("e.g. 2026-03-15", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueAccent,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = LightBlueTint,
                        unfocusedContainerColor = LightBlueTint
                    ),
                    singleLine = true
                )
            }

            // Time
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Incident Time (optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = incidentTime,
                    onValueChange = { incidentTime = it },
                    placeholder = { Text("e.g. 14:30", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueAccent,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = LightBlueTint,
                        unfocusedContainerColor = LightBlueTint
                    ),
                    singleLine = true
                )
            }

            // Location
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Location (optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Where did it happen? (optional)", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueAccent,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = LightBlueTint,
                        unfocusedContainerColor = LightBlueTint
                    ),
                    singleLine = true
                )

                Text(
                    text = "Describe the place of the incident, not where you are now.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onNext(incidentDate, incidentTime, location)
                },
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
                        text = "Next: Review & Submit",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
        }
    }
}
