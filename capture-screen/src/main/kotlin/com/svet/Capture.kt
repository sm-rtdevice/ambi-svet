package com.svet

import com.svet.program.Svet
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

suspend fun main(args: Array<String>) {
    log.info {
        if (args.isNotEmpty()) { "Ambient svet capture started with params: ${args.joinToString()}" }
        else { "Ambient svet capture started" }
    }

    Svet().run(args)
}
