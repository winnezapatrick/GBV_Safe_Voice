package com.example.gbvapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbvapp.ui.theme.BlueAccent
import com.example.gbvapp.ui.theme.LightBlueTint
import com.example.gbvapp.ui.theme.NavyPrimary

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (textColor, bgColor) = when (status.lowercase()) {
        "pending" -> Color(0xFFB45309) to Color(0xFFFEF3C7)
        "assigned" -> Color(0xFF1D4ED8) to Color(0xFFDBEAFE)
        "open" -> BlueAccent to Color(0xFFE0F2FE)
        "resolved" -> Color(0xFF15803D) to Color(0xFFDCFCE7)
        "closed" -> Color(0xFF475569) to Color(0xFFF1F5F9)
        else -> NavyPrimary to LightBlueTint
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
