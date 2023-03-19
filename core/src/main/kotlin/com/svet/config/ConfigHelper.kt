package com.svet.config

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

/**
 * Загрузка/сохранение конфигурации.
 **/
class ConfigHelper {

    companion object {
        fun <T> loadConfig(fileName: String, valueType: Class<T>, defaultConfig: T): T {

            val config: T = try {
                ObjectMapper().readValue(Paths.get(fileName).toFile(), valueType)
            } catch (e: Exception) {
                logger.info("No valid configuration found. Create config by default.")
                // TODO: save if not exist config file (if flag == true)
                defaultConfig
            }

            return config
        }

        fun <T> saveConfig(fileName: String, config: T) {
            try {
                ObjectMapper().writeValue(Paths.get(fileName).toFile(), config)
            } catch (e: Exception) {
                logger.error("Error during saving configuration.")
            }
        }
    }
}