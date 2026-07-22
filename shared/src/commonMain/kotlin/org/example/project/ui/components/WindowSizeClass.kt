package org.example.project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier

enum class WindowSizeClass {
    COMPACT, MEDIUM, EXPANDED
}

@Composable
fun calculateWindowSizeClass(width: Dp): WindowSizeClass {
    return when {
        width < 600.dp -> WindowSizeClass.COMPACT
        width < 840.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

@Composable
fun AdaptiveContent(
    content: @Composable (WindowSizeClass) -> Unit
) {
    BoxWithConstraints {
        val windowSizeClass = calculateWindowSizeClass(maxWidth)
        content(windowSizeClass)
    }
}

@Composable
fun AdaptiveScreenWrapper(
    sizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
    maxWidth: Dp = 800.dp,
    content: @Composable () -> Unit
) {
    if (sizeClass == WindowSizeClass.EXPANDED) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.TopCenter) {
            Box(modifier = modifier.widthIn(max = maxWidth).fillMaxHeight()) {
                content()
            }
        }
    } else {
        content()
    }
}
