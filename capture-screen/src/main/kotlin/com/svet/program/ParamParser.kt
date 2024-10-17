package com.svet.program

import com.svet.enums.ExitStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.system.exitProcess

private val log = KotlinLogging.logger {}

object ParamParser {
    fun parseOptional(args: Array<String>, index: Int): Int? {
        return if (args.size > index) args[index].toIntOrNull()
        else null
    }

    fun parse(args: Array<String>, index: Int): Int {
        val param = parseOptional(args, index)
        if (param == null) {
            log.warn { "Missing param by index $index" }
            exitProcess(ExitStatus.MISSING_PARAM_BY_INDEX.status)
        }
        return param
    }
}
