package com.amrubio27.cursotestingandroid.core.mockwebserver.rules

import com.amrubio27.cursotestingandroid.core.mockwebserver.MockWebServerUrlHolder
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MockWebServerRule : TestWatcher() {
    val server = MockWebServer()

    override fun starting(description: Description?) {
        super.starting(description)
        server.start()
        MockWebServerUrlHolder.baseUrl = server.url("/").toString()
    }

    override fun finished(description: Description?) {
        super.finished(description)
        server.shutdown()
        super.finished(description)
    }
}