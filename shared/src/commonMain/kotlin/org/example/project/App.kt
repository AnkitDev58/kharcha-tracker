package org.example.project

import androidx.compose.runtime.Composable
import org.example.project.ui.navigation.MainNavigation
import org.example.project.ui.theme.TrackerTheme

@Composable
fun App() {
    TrackerTheme {
        MainNavigation()
    }
}
