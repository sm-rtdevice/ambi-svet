package com.svet.capture

import com.svet.command.Command
import com.svet.config.SvetConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.UnsupportedEncodingException
import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

private val log = KotlinLogging.logger {}

class CommandProcessor {
    private val svetConfig = SvetConfig()
    private var serialPort: SerialPort? = null
    private var job: Job = Job()
    private var scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Volatile
    private var doWork = true

    fun init() {
        log.debug { "Start initialization..." }

        if (svetConfig.connectConfig.detectPorts) {
            val portNames = SerialPortList.getPortNames()
            if (portNames.isNotEmpty()) {
                for (portName in portNames) {
                    log.info { "Available com port: $portName" }
                }
            } else {
                log.info { "No available com ports found" }
                return
            }
        }

        serialPort = SerialPort(svetConfig.connectConfig.portNumber)

        log.debug { "Initialization done" }
    }

    private fun detect() {
        log.warn { "Command not supported yet" }
    }

    fun connect() {
        if (serialPort == null) {
            log.warn { "Port ${svetConfig.connectConfig.portNumber} is not available" }
            doWork = false
            return
        }

        log.info { "Connect to port ${svetConfig.connectConfig.portNumber}" }

        var fault = true
        try {
            serialPort!!.openPort()
            serialPort!!.setParams(
                SerialPort.BAUDRATE_115200,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE
            )
            fault = false
        } catch (ex: UninitializedPropertyAccessException) {
            log.error(ex) {
                "Uninitialized property exception during connect to port ${svetConfig.connectConfig.portNumber}"
            }
        } catch (ex: SerialPortException) {
            log.error(ex) { "Serial port exception during connect to port ${svetConfig.connectConfig.portNumber}" }
        } catch (ex: UnsupportedEncodingException) {
            log.error(ex) {
                "Unsupported encoding exception during connect to port ${svetConfig.connectConfig.portNumber}"
            }
        } catch (ex: Exception) {
            log.error(ex) { "Connect to port ${svetConfig.connectConfig.portNumber}" }
        }

        doWork = if (!fault) {
            Thread.sleep(svetConfig.connectConfig.arduinoRebootTimeout)
            log.info { "Connect to port ${svetConfig.connectConfig.portNumber} success" }
            true
        } else {
            false
        }
    }

    suspend fun disconnect() {
        doWork = false
        job.join()

        if (serialPort == null) {
            return
        }

        log.info { "Disconnect from port ${svetConfig.connectConfig.portNumber}" }
        try {
            serialPort?.closePort()
        } catch (ex: SerialPortException) {
            log.error(ex) { "Disconnect from port ${svetConfig.connectConfig.portNumber}" }
            return
        }

        log.info { "Disconnect from port ${svetConfig.connectConfig.portNumber} success" }
    }

    suspend fun reconnect() {
        log.info { "Reconnect to port ${svetConfig.connectConfig.portNumber}" }
        disconnect()
        connect()
    }

    /**
     * Запустить задачу.
     * @param command команда для выполнения
     **/
    suspend fun launch(command: Command) {
        if (serialPort == null) {
            log.warn { "COM port is not available, task ${command.name()} is not launched" }
            return
        }

        job = scope.launch {
            while (doWork) {
                val elapsedTime = measureTimeMillis {
                    serialPort?.writeBytes(command.buffer())
                    delay(1)
                }
                print("\rFPS: ${getFps(elapsedTime)}; elapsed: $elapsedTime ms")
            }
            log.info { "Task ${command.name()} stopped" }
        }

        log.info { "Task ${command.name()} launched" }
    }

    /**
     * Выполнить команду.
     * @param command команда для выполнения
     **/
    fun exec(command: Command) {
        if (serialPort == null) {
            log.warn { "COM port is not available, command ${command.name()} is not executed" }
            return
        }

        serialPort?.writeBytes(command.buffer())
    }

    private fun getFps(elapsedTime: Long): Long {
        return if (elapsedTime == 0L) {
            0L
        } else {
            1000L / elapsedTime
        }
    }
}
