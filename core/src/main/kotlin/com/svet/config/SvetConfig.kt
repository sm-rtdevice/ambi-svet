package com.svet.config

import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths

private val log = KotlinLogging.logger {}

/**
 * Конфигурация приложения.
 **/
object SvetConfig {

    private const val CONFIG_DIRECTORY = "config"
    private const val CONNECT_CONFIG_FILE_NAME = "connect-config.json"
    private const val CAPTURE_CONFIG_FILE_NAME = "capture-config.json"

    fun connectConfig(): ConnectConfig {
        log.debug { "Loading connect configuration..." }
        val config = ConfigHelper.load(
            resolveConfigFileName(CONNECT_CONFIG_FILE_NAME),
            ConnectConfig::class.java
        ) ?: ConnectConfig()

        log.debug { "Loading connect configuration done" }
        return config
    }

    fun connectConfig(config: ConnectConfig) {
        Files.createDirectories(Paths.get(CONFIG_DIRECTORY))
        ConfigHelper.save(resolveConfigFileName(CONNECT_CONFIG_FILE_NAME), config)
    }

    fun captureConfig(): CaptureConfig {
        log.debug { "Loading capture configuration..." }
        val config = ConfigHelper.load(
            resolveConfigFileName(CAPTURE_CONFIG_FILE_NAME),
            CaptureConfig::class.java
        ) ?: CaptureConfig.defaultConfig()

        if (config.positions.isEmpty()) {
            log.warn { "Capture regions configuration was not loaded" }
        }

        log.debug { "Loading capture configuration done" }
        return config
    }

    fun captureConfig(config: CaptureConfig) {
        Files.createDirectories(Paths.get(CONFIG_DIRECTORY))
        ConfigHelper.save(resolveConfigFileName(CAPTURE_CONFIG_FILE_NAME), config)
    }

    private fun resolveConfigFileName(fileName: String): String {
        return CONFIG_DIRECTORY + "\\" + fileName
    }

}
