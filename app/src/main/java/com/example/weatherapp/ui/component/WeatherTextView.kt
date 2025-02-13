package com.example.weatherapp.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun WeatherTextView(text: String, size: TextUnit) {
    Text(text = text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = size,
        textAlign = TextAlign.Center)
}