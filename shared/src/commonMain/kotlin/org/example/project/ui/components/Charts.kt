package org.example.project.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import org.example.project.ui.theme.ChartColors

data class ChartDataPoint(
    val label: String,
    val value: Float,
    val color: Color? = null,
    val colors: List<Color>? = null
)

@Composable
fun DonutChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 36.dp,
    centerContent: @Composable (BoxScope.() -> Unit)? = null
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat().takeIf { it > 0 } ?: 1f
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "donut"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val diameter = this.size.minDimension - strokePx
            val topLeft = Offset(strokePx / 2, strokePx / 2)
            val arcSize = Size(diameter, diameter)
            var startAngle = -90f

            data.forEachIndexed { index, point ->
                val sweep = (point.value / total) * 360f * animationProgress
                val color = point.color ?: ChartColors.getOrElse(index) { ChartColors[0] }
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep - 2f, // gap
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(strokePx, cap = StrokeCap.Round)
                )
                startAngle += (point.value / total) * 360f
            }
        }
        if (centerContent != null) {
            centerContent()
        }
    }
}

//@Composable
//fun BarChart(
//    data: List<ChartDataPoint>,
//    modifier: Modifier = Modifier,
//    barColor: Color? = null,
//    showLabels: Boolean = true,
//    maxHeight: Dp = 120.dp
//) {
//    val maxValue = data.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1f
//    val animationProgress by animateFloatAsState(
//        targetValue = 1f,
//        animationSpec = tween(900, easing = FastOutSlowInEasing),
//        label = "bar"
//    )
//
//    Row(
//        modifier = modifier.height(maxHeight + if (showLabels) 24.dp else 0.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.Bottom
//    ) {
//        data.forEachIndexed { index, point ->
//            val heightFraction = (point.value / maxValue) * animationProgress
//            val color = barColor ?: point.color ?: ChartColors.getOrElse(index) { ChartColors[0] }
//
//            Column(
//                modifier = Modifier.weight(1f),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Bottom
//            ) {
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxWidth()
//                        .padding(horizontal = 4.dp),
//                    contentAlignment = Alignment.BottomCenter
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight(heightFraction)
//                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
//                            .background(
//                                Brush.verticalGradient(listOf(color.copy(alpha = 0.7f), color))
//                            )
//                    )
//                }
//                if (showLabels) {
//                    Spacer(Modifier.height(4.dp))
//                    Text(
//                        text = point.label,
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun BarChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    showLabels: Boolean = false,
    maxHeight: Dp = 120.dp,
    barWidth: Dp = 25.dp,
    barSpacing: Dp = 8.dp,
    onClick: (String) -> Unit
) {
    val maxValue = data.maxOfOrNull { it.value }?.takeIf { it > 0f } ?: 1f

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "bar"
    )

    val scrollState = rememberScrollState()

    // Optional: Scroll to latest values
    LaunchedEffect(data.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .height(maxHeight + if (showLabels) 24.dp else 0.dp),
        horizontalArrangement = Arrangement.spacedBy(barSpacing),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, point ->
            val heightFraction = (point.value / maxValue) * animationProgress
            val color = point.color
                ?: ChartColors.getOrElse(index) { ChartColors.first() }
            Column(
                modifier = Modifier.width(barWidth).clickable {
                    onClick.invoke(point.label)
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .height(maxHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(heightFraction)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 6.dp,
                                    topEnd = 6.dp
                                )
                            )
                            .background(
                                Brush.verticalGradient(

                                    if ((point.colors?.size ?: 0) > 1) {
                                        point.colors ?: listOf(
                                            color.copy(alpha = 0.7f),
                                            color
                                        )
                                    } else {
                                        listOf(
                                            color.copy(alpha = 0.7f),
                                            color
                                        )
                                    }
                                )
                            )
                    )


                    Text(
                        text = point.label.formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )

                }

                if (showLabels) {
                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = point.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LineChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillGradient: Boolean = true,
    showDots: Boolean = true
) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "line"
    )

    if (data.isEmpty()) return

    val maxValue = data.maxOf { it.value }.takeIf { it > 0 } ?: 1f

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val stepX = if (data.size > 1) width / (data.size - 1) else width

        val points = data.mapIndexed { i, point ->
            Offset(
                x = i * stepX,
                y = height - (point.value / maxValue) * height * animationProgress
            )
        }

        // Fill gradient
        if (fillGradient && points.size > 1) {
            val fillPath = Path().apply {
                moveTo(points.first().x, height)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent)
                )
            )
        }

        // Line
        if (points.size > 1) {
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val cp1x = (points[i - 1].x + points[i].x) / 2
                    cubicTo(cp1x, points[i - 1].y, cp1x, points[i].y, points[i].x, points[i].y)
                }
            }
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Dots
        if (showDots) {
            points.forEach { point ->
                drawCircle(color = lineColor, radius = 6f, center = point)
                drawCircle(color = Color.White, radius = 3f, center = point)
            }
        }
    }
}

@Composable
fun ChartLegend(
    items: List<Pair<String, Color>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { (label, color) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

val formatter = LocalDate.Format {
    dayOfMonth()
    char('\n')
    monthName(kotlinx.datetime.format.MonthNames.ENGLISH_ABBREVIATED)
    char('\n')
    year()
}

val String.formattedTime get() = LocalDate.parse(this).format(formatter)