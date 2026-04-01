package com.amrubio27.cursotestingandroid.core.presentation.ext

import java.util.Locale

fun Double.toPriceAmount(): String {
    return String.format(Locale.getDefault(), "%.2f €", this)
}