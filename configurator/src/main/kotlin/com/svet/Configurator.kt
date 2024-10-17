package com.svet

import com.svet.frame.CaptureConfigFrame
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

fun main(args: Array<String>) {
    log.info {
        if (args.isNotEmpty()) { "Ambient svet configurator started with arguments: ${args.joinToString()}" }
        else { "Ambient svet configurator started" }
    }

    CaptureConfigFrame()
}
