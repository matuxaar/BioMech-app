package com.biomech.core.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun EMGChart(
    data: List<List<Float>>,
    modifier: Modifier = Modifier,
    channelLabels: List<String> = listOf("CH1", "CH2", "CH3", "CH4", "CH5", "CH6", "CH7", "CH8"),
) {
    val colors = listOf(
        Color(0xFFE53935), Color(0xFF43A047), Color(0xFF1E88E5), Color(0xFFFB8C00),
        Color(0xFF8E24AA), Color(0xFF00ACC1), Color(0xFF6D4C41), Color(0xFF546E7A),
    )

    Column(modifier = modifier) {
        data.forEachIndexed { index, channelData ->
            if (channelData.isNotEmpty()) {
                ChannelRow(
                    label = channelLabels.getOrElse(index) { "CH${index + 1}" },
                    data = channelData,
                    lineColor = colors.getOrElse(index) { Color.Gray },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                )
            }
        }
    }
}

@Composable
private fun ChannelRow(
    label: String,
    data: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = lineColor,
            modifier = Modifier.width(32.dp),
        )

        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val spacing = size.width / (data.size - 1).coerceAtLeast(1)
            val midY = size.height / 2
            val amplitude = size.height / 2 * 0.85f

            drawLine(
                Color(0xFFE0E0E0),
                Offset(0f, midY),
                Offset(size.width, midY),
                strokeWidth = 0.5f,
            )

            if (data.size < 2) return@Canvas

            val path = Path().apply {
                moveTo(0f, midY - (data[0] / 3.3f) * amplitude)
                for (i in 1 until data.size) {
                    lineTo(i * spacing, midY - (data[i] / 3.3f) * amplitude)
                }
            }

            drawPath(path, lineColor, style = Stroke(width = 1.5f))
        }
    }
}
