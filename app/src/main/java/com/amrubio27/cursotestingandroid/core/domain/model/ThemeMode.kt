package com.amrubio27.cursotestingandroid.core.domain.model

sealed class ThemeMode(val id: Int) {
    object SYSTEM : ThemeMode(0)
    object LIGHT : ThemeMode(1)
    object DARK : ThemeMode(2)

}