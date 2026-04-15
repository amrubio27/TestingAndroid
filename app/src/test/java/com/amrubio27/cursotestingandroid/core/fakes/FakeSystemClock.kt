package com.amrubio27.cursotestingandroid.core.fakes

import com.amrubio27.cursotestingandroid.core.domain.util.Clock
import java.time.Instant

class FakeSystemClock() : Clock {
    private var currentTime: Instant = Instant.now()

    override fun now(): Instant = currentTime

    fun advanceTimeBy(seconds: Long) {
        currentTime = currentTime.plusSeconds(seconds)
    }

    fun setTime(instant: Instant) {
        currentTime = instant
    }
}