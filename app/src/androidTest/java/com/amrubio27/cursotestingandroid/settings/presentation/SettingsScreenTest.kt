package com.amrubio27.cursotestingandroid.settings.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import kotlin.test.Test


class SettingsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun firstUITest() {
        composeRule.setContent {
            SettingsContent(
                uiState = SettingsUiState(),
                onBack = {},
                onInStockOnlyChange = {},
                onThemeModeSelected = {}
            )
        }
        composeRule.onNodeWithText("Ajustes").assertIsDisplayed()
    }

}