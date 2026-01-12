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
    val connectConfig = SvetConfig.connectConfig()
    private var serialPort: SerialPort? = null
    private var job: Job = Job()
    private var scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Volatile
    private var doWork = true

    fun init() {
        log.debug { "Start initialization..." }

        if (connectConfig.detectPorts) {
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

        serialPort = SerialPort(connectConfig.portNumber)

        log.debug { "Initialization done" }
    }

    private fun detect() {
        log.warn { "Command not supported yet" }
    }

    fun connect() {
        val port = serialPort
        if (port == null) {
            log.warn { "Port ${connectConfig.portNumber} is not available" }
            doWork = false
            return
        }

        log.info { "Connect to port ${connectConfig.portNumber}" }

        var fault = true
        try {
            port.openPort()
            port.setParams(
                SerialPort.BAUDRATE_115200,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE
            )
            fault = false
        } catch (ex: UninitializedPropertyAccessException) {
            log.error(ex) {
                "Uninitialized property exception during connect to port ${connectConfig.portNumber}"
            }
        } catch (ex: SerialPortException) {
            log.error(ex) { "Serial port exception during connect to port ${connectConfig.portNumber}" }
        } catch (ex: UnsupportedEncodingException) {
            log.error(ex) {
                "Unsupported encoding exception during connect to port ${connectConfig.portNumber}"
            }
        } catch (ex: Exception) {
            log.error(ex) { "Connect to port ${connectConfig.portNumber}" }
        }

        doWork = if (!fault) {
            Thread.sleep(connectConfig.arduinoRebootTimeout)
            log.info { "Connect to port ${connectConfig.portNumber} success" }
            true
        } else {
            false
        }
    }

    suspend fun disconnect() {
        doWork = false
        job.join()

        val port = serialPort ?: return

        log.info { "Disconnect from port ${connectConfig.portNumber}" }
        try {
            port.closePort()
        } catch (ex: SerialPortException) {
            log.error(ex) { "Disconnect from port ${connectConfig.portNumber}" }
            return
        }

        log.info { "Disconnect from port ${connectConfig.portNumber} success" }
    }

    suspend fun reconnect() {
        log.info { "Reconnect to port ${connectConfig.portNumber}" }
        disconnect()
        connect()
    }

    /**
     * Запустить задачу.
     * @param command команда для выполнения
     **/
    fun launch(command: Command) {
        val port = serialPort
        if (port == null) {
            log.warn { "COM port is not available, task ${command.name()} is not launched" }
            return
        }

        job = scope.launch {
            while (doWork) {
                val elapsedTime = measureTimeMillis {
                    port.writeBytes(command.buffer())
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
        val port = serialPort
        if (port == null) {
            log.warn { "COM port is not available, command ${command.name()} is not executed" }
            return
        }

        port.writeBytes(command.buffer())
    }

    private fun getFps(elapsedTime: Long): Long {
        return if (elapsedTime == 0L) {
            0L
        } else {
            1000L / elapsedTime
        }
    }
}
