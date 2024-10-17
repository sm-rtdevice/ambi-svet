package com.svet.program

import com.svet.capture.CommandProcessor
import com.svet.command.Blink
import com.svet.command.Capture
import com.svet.command.Gradient
import com.svet.command.RandomColor
import com.svet.command.SolidColor
import com.svet.command.StartupMode
import com.svet.enums.ExitStatus
import com.svet.enums.ProgramMode
import com.svet.enums.ProgramMode.CAPTURE
import com.svet.enums.ProgramMode.RANDOM_SCENE
import com.svet.enums.ProgramMode.SET_SOLID_COLOR
import com.svet.enums.ProgramMode.SET_STARTUP_MODE
import com.svet.enums.ProgramMode.TEST_MODE
import com.svet.enums.ProgramMode.BLINK
import com.svet.enums.ProgramMode.GRADIENT
import com.svet.enums.ProgramMode.INIT
import com.svet.enums.ProgramMode.CONNECT
import com.svet.enums.ProgramMode.RECONNECT
import com.svet.enums.ProgramMode.DISCONNECT
import com.svet.enums.ProgramMode.EXIT_PROGRAM
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Color
import kotlin.system.exitProcess

private val log = KotlinLogging.logger {}

class Svet {
    private val commandProcessor: CommandProcessor = CommandProcessor()

    suspend fun run(args: Array<String>) {
        commandProcessor.init()
        commandProcessor.connect()

        when (ProgramMode.from(ParamParser.parseOptional(args, 0))) {
            CAPTURE -> commandProcessor.launch(Capture())
            SET_SOLID_COLOR -> {
                val r = ParamParser.parse(args, 1)
                val g = ParamParser.parse(args, 2)
                val b = ParamParser.parse(args, 3)
                val save = ParamParser.parse(args, 4)
                if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                    log.warn { "Invalid RGB params" }
                    exitProcess(ExitStatus.INVALID_PARAM.status)
                }
                // запомнить цвет подсветки, for example program arguments: 2 128 92 30 1
                commandProcessor.exec(SolidColor(Color(r, g, b), save == 1))
                log.info { "Set solid color is done" }
                exitProcess(ExitStatus.OK.status)
            }
            SET_STARTUP_MODE -> { // включение монотонной подсветки при старте, for example program arguments: 3 1
                val on = ParamParser.parse(args, 1)
                if (on == 0 || on == 1) {
                    commandProcessor.exec(StartupMode(on.toByte()))
                    log.info { "Set startup mode '$on' is done" }
                } else {
                    log.warn { "Unknown startup mode '$on' not set. Available: 0 (off) or 1 (on)" }
                }
                exitProcess(ExitStatus.OK.status)
            }
            INIT -> {
//            commandProcessor.init() // not supported yet
                log.warn { "Command not supported yet" }
                exitProcess(ExitStatus.NOT_SUPPORTED.status)
            }
            CONNECT -> {
//            commandProcessor.connect()
                log.warn { "Command not supported yet" }
                exitProcess(ExitStatus.NOT_SUPPORTED.status)
            }
            RECONNECT -> {
                commandProcessor.reconnect()
            }
            DISCONNECT -> {
                commandProcessor.disconnect()
            }
            EXIT_PROGRAM -> {
                exitProcess(ExitStatus.OK.status)
            }
            RANDOM_SCENE -> commandProcessor.launch(RandomColor())
            BLINK -> {
                commandProcessor.launch(Blink())
            }
            GRADIENT -> {
                commandProcessor.launch(Gradient())
            }
            TEST_MODE -> {
                // testing development mode
            }
        }

        val exitCmd = readlnOrNull()

        commandProcessor.disconnect()
        log.info { "End program with command: $exitCmd" }
    }
}
