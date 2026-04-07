package com.amrubio27.cursotestingandroid.core.presentation.ext

import org.junit.Assert.assertEquals
import org.junit.Test

class DoubleExtTest {
    @Test
    fun roundTo2Decimals_roundsCorrectly() {
        //test de ejemplo
        assertEquals(2.35, 2.3456.roundTo2Decimals(), 0.0)
        //se pueden testear a la vez en el mismo test varias casuisticas
    }
}