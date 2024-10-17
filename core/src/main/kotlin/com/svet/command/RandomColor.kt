package com.svet.command

import com.svet.config.SvetConfig
import com.svet.utils.Utils

/**
 * Команда контроллера для вывода случайного цвета.
 **/
class RandomColor : Command {

    private val svetConfig = SvetConfig() // todo: singleton только svetConfig.captureConfig

    override fun name(): String {
        return "Random Color"
    }

    override fun buffer(): ByteArray {
        return Utils.preparerRandomBuffer(svetConfig.captureConfig).toByteArray()
    }
}
