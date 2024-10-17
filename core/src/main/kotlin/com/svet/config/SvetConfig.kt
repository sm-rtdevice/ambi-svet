package com.svet.config

import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths

private val log = KotlinLogging.logger {}

/**
 * Конфигурация приложения.
 **/
class SvetConfig {

    private val configDirectory = "config"
    private val connectConfigFileName = "connect-config.json"
    private val captureConfigFileName = "capture-config.json"

    lateinit var connectConfig: ConnectConfig
    lateinit var captureConfig: CaptureConfig

    init {
        loadConfigs()
    }

    fun loadConfigs() {
        loadConnectConfig()
        loadCaptureConfig()
    }

    fun saveConfigs() {
        saveConnectConfig()
        saveCaptureConfig()
    }

    private fun loadConnectConfig() {
        log.debug { "Loading connect configuration..." }

        connectConfig = ConfigHelper.loadConfig(
            resolveConfigFileName(connectConfigFileName),
            ConnectConfig::class.java,
            ConnectConfig() // TODO: ConnectConfig.defaultConfig()
        )

        log.debug { "Loading connect configuration done" }
    }

    private fun loadCaptureConfig() {
        log.debug { "Loading capture configuration..." }

        captureConfig = ConfigHelper.loadConfig(
            resolveConfigFileName(captureConfigFileName),
            CaptureConfig::class.java,
            CaptureConfig.defaultConfig()
        )

        if (captureConfig.positions.isEmpty()) {
            log.warn { "Capture regions configuration was not loaded" }
        }

        log.debug { "Loading capture configuration done" }
    }

    fun saveConnectConfig() {
        Files.createDirectories(Paths.get(configDirectory))
        ConfigHelper.saveConfig(resolveConfigFileName(connectConfigFileName), connectConfig)
    }

    fun saveCaptureConfig() {
        Files.createDirectories(Paths.get(configDirectory))
        ConfigHelper.saveConfig(resolveConfigFileName(captureConfigFileName), captureConfig)
    }

    private fun resolveConfigFileName(fileName: String): String {
        return configDirectory + "\\" + fileName
    }

}
