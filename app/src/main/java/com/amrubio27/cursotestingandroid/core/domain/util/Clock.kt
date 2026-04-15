package com.amrubio27.cursotestingandroid.core.domain.util

import java.time.Instant

interface Clock {
    fun now(): Instant
}