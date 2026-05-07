package com.amrubio27.cursotestingandroid.core.utils

import app.cash.turbine.ReceiveTurbine

//forma2
suspend fun <T> ReceiveTurbine<T>.awaitStateMatching(
    predicate: (T) -> Boolean
): T {
    while (true) {
        val item: T = awaitItem()
        if (predicate(item)) return item
    }
}