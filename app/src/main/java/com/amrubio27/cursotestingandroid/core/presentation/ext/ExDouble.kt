package com.amrubio27.cursotestingandroid.core.presentation.ext

import kotlin.math.roundToInt

fun Double.roundTo2Decimals(): Double {
    return (this * 100).roundToInt() / 100.0
}
