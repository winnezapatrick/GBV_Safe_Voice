package com.example.gbvapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbvapp.ui.theme.AppBackground
import com.example.gbvapp.ui.theme.NavyPrimary
import com.example.gbvapp.ui.theme.TextPrimary
import com.example.gbvapp.ui.theme.TextSecondary

@Composable
fun IncidentDetailScreen(
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Incident Details",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Case information and tracking details will appear here.",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
            Text(
                text = "Back",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
