package com.amrubio27.cursotestingandroid.settings.presentation

import com.amrubio27.cursotestingandroid.core.MainDispatcherRule
import com.amrubio27.cursotestingandroid.core.fakes.FakeSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun exampleTestCrash() = runTest {
        val viewModel = SettingsViewModel(FakeSettingsRepository())

        viewModel.setInStockOnly(true)

        assertTrue(viewModel.uiState.value.inStockOnly)
    }

    @Test
    fun secondExample() = runTest(mainDispatcherRule.scheduler) {
        val settingsRepository = FakeSettingsRepository().apply {
            setInStockOnly(true)
        }

        val viewModel = SettingsViewModel(settingsRepository)
        advanceUntilIdle()
        assertTrue((viewModel.uiState.value.inStockOnly))
    }
}