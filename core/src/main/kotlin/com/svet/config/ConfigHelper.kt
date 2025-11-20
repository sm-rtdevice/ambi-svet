package com.svet.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Paths

private val log = KotlinLogging.logger {}

/**
 * Загрузка/сохранение конфигурации.
 **/
object ConfigHelper {

    fun <T> load(fileName: String, valueType: Class<T>): T? {
        val config: T? = try {
            ObjectMapper().readValue(Paths.get(fileName).toFile(), valueType)
        } catch (_: Exception) {
            log.debug { "No valid configuration ${valueType.name} found. Create by default." }
            null
        }

        return config
    }

    fun <T> save(fileName: String, config: T) {
        try {
            ObjectMapper().writeValue(Paths.get(fileName).toFile(), config)
        } catch (e: Exception) {
            log.error(e) { "Error during saving configuration ${config?.javaClass?.name}" }
        }
    }
}
