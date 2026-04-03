package com.amrubio27.cursotestingandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amrubio27.cursotestingandroid.core.domain.model.ThemeMode
import com.amrubio27.cursotestingandroid.core.presentation.navigation.NavGraph
import com.amrubio27.cursotestingandroid.ui.theme.CursoTestingAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle(
                initialValue = ThemeMode.SYSTEM
            )

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true

            }

            CursoTestingAndroidTheme(
                darkTheme = darkTheme
            ) {
                NavGraph()
            }
        }
    }
}