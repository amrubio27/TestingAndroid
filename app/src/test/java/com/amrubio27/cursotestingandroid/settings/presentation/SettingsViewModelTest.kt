package com.amrubio27.cursotestingandroid.settings.presentation

import app.cash.turbine.test
import com.amrubio27.cursotestingandroid.core.MainDispatcherRule
import com.amrubio27.cursotestingandroid.core.domain.model.ThemeMode
import com.amrubio27.cursotestingandroid.core.fakes.FakeSettingsRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `GIVEN repository with values WHEN viewmodel is initialized THEN uistate is updated`() =
        runTest(mainDispatcherRule.scheduler) {
            val settingsRepository = FakeSettingsRepository().apply {
                setInStockOnly(true)
            }

            val viewModel = SettingsViewModel(settingsRepository)

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state.inStockOnly)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given viewmodel when theme mode is changed then ui stated and repository are updated`() =
        runTest(mainDispatcherRule.scheduler) {
            //GIVEN
            val settingsRepository = FakeSettingsRepository()
            val viewModel = SettingsViewModel(settingsRepository)

            viewModel.uiState.test {
                awaitItem()

                //WHEN
                viewModel.setThemeMode(ThemeMode.DARK)

                //THEN
                val updateState = awaitItem()
                assertEquals(ThemeMode.DARK, updateState.themeMode)

                assertEquals(ThemeMode.DARK, settingsRepository.themeMode.first())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given viewmodel when in stock only is changed then ui stated and repository are updated`() =
        runTest(mainDispatcherRule.scheduler) {
            //GIVEN
            val settingsRepository = FakeSettingsRepository()
            val viewModel = SettingsViewModel(settingsRepository)

            viewModel.uiState.test {
                awaitItem()

                //WHEN
                viewModel.setInStockOnly(true)

                //THEN
                val updateState = awaitItem()
                assertEquals(true, updateState.inStockOnly)

                assertEquals(true, settingsRepository.inStockOnly.first())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given viewmodel when repository change externally when ui state update automatically`() =
        runTest(mainDispatcherRule.scheduler) {
            val settingsRepository = FakeSettingsRepository()
            val viewModel = SettingsViewModel(settingsRepository)

            viewModel.uiState.test {
                awaitItem()

                settingsRepository.setInStockOnly(true)

                assertTrue(awaitItem().inStockOnly)

                cancelAndIgnoreRemainingEvents()
            }
        }
}