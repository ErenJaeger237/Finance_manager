package com.nts.financemanager.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Simulated Glassmorphism approach
fun Modifier.glassmorphic(
    backgroundColor: Color = VaultSurfaceHighest.copy(alpha = 0.6f),
    borderColor: Color = VaultOnSurface.copy(alpha = 0.1f),
    cornerRadius: Dp = 24.dp
): Modifier = composed {
    this
        .background(backgroundColor, RoundedCornerShape(cornerRadius))
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    borderColor,
                    Color.Transparent
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}

// Faux Neomorphism with Outer Shadow
fun Modifier.neomorphic(
    shadowColor: Color = Color.Black.copy(alpha = 0.3f), // Assuming dark mode context
    highlightColor: Color = Color.White.copy(alpha = 0.05f),
    cornerRadius: Dp = 16.dp,
    offsetY: Dp = 4.dp,
    offsetX: Dp = 0.dp,
    blurRadius: Dp = 12.dp
): Modifier = composed {
    this.drawBehind {
        val shadowPaint = Paint().apply {
            color = Color.Transparent
            val frameworkPaint = asFrameworkPaint()
            frameworkPaint.color = Color.Transparent.toArgb()
            frameworkPaint.setShadowLayer(
                blurRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor.toArgb()
            )
        }
        
        val highlightPaint = Paint().apply {
            color = Color.Transparent
            val frameworkPaint = asFrameworkPaint()
            frameworkPaint.color = Color.Transparent.toArgb()
            frameworkPaint.setShadowLayer(
                blurRadius.toPx(),
                -offsetX.toPx(),
                -offsetY.toPx(),
                highlightColor.toArgb()
            )
        }

        drawIntoCanvas { canvas ->
            // Draw Highlight (Top/Left)
            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = cornerRadius.toPx(),
                radiusY = cornerRadius.toPx(),
                paint = highlightPaint
            )
            // Draw Shadow (Bottom/Right)
            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = cornerRadius.toPx(),
                radiusY = cornerRadius.toPx(),
                paint = shadowPaint
            )
        }
    }
}
