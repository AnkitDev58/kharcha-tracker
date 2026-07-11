package org.example.project.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.theme.ProgressGreen
import org.example.project.ui.theme.ProgressOrange
import org.example.project.ui.theme.ProgressRed
import org.example.project.ui.theme.ProgressYellow

@Composable
fun AnimatedCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    trackColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
    label: String? = null,
    centerContent: @Composable (BoxScope.() -> Unit)? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val progressColor = getProgressColor(progress)
    val gradientColors = getProgressGradient(progress)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val diameter = this.size.minDimension - strokePx
            val topLeft = Offset(strokePx / 2, strokePx / 2)
            val arcSize = Size(diameter, diameter)

            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokePx, cap = StrokeCap.Round)
            )

            // Progress with gradient
            drawArc(
                brush = Brush.sweepGradient(
                    colors = gradientColors,
                    center = Offset(this.size.width / 2, this.size.height / 2)
                ),
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokePx, cap = StrokeCap.Round)
            )
        }

        if (centerContent != null) {
            centerContent()
        } else if (label != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                )
                if (label.isNotEmpty()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    trackColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
    showLabel: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "linear_progress"
    )
    val gradientColors = progressBrush(trackColor.copy(alpha = 1f))
    val pro = progressBrushAlpha15(trackColor.copy(alpha = 0.15f))
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cornerRadius = height.toPx() / 2

                // Track
                drawRoundRect(
                    brush = pro,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
                    size = this.size
                )

                // Progress
                if (animatedProgress > 0f) {
                    drawRoundRect(
                        brush = gradientColors,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
                        size = Size(this.size.width * animatedProgress, this.size.height)
                    )
                }
            }
        }
        if (showLabel) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = getProgressColor(progress)
            )
        }
    }
}

fun getProgressColor(progress: Float): Color = when {
    progress >= 0.9f -> ProgressRed
    progress >= 0.7f -> ProgressOrange
    progress >= 0.5f -> ProgressYellow
    else -> ProgressGreen
}

fun getProgressGradient(progress: Float): List<Color> = when {
    progress >= 0.9f -> listOf(ProgressRed, Color(0xFFFF4040))
    progress >= 0.7f -> listOf(ProgressOrange, ProgressRed)
    progress >= 0.5f -> listOf(ProgressYellow, ProgressOrange)
    else -> listOf(ProgressGreen, Color(0xFF44EE77))
}


private fun progressBrush(trackColor: Color): Brush {
    return Brush.horizontalGradient(
        colorStops = arrayOf(
            0.00f to trackColor,
            0.45f to trackColor,
            0.50f to ProgressGreen,
            0.50f to ProgressGreen,
            0.70f to ProgressYellow,
            0.90f to ProgressOrange,
            1.00f to ProgressRed
        )
    )
}
private fun progressBrushAlpha15(trackColor: Color): Brush {
    return Brush.horizontalGradient(
        colorStops = arrayOf(
            0.00f to trackColor,
            0.45f to trackColor,
            0.50f to ProgressGreen.copy(alpha = 0.15f),
            0.50f to ProgressGreen.copy(alpha = 0.15f),
            0.70f to ProgressYellow.copy(alpha = 0.15f),
            0.90f to ProgressOrange.copy(alpha = 0.15f),
            1.00f to ProgressRed.copy(alpha = 0.15f)
        )
    )
}