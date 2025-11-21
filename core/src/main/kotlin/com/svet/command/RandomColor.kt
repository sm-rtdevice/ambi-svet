package com.svet.command

import com.svet.config.SvetConfig
import com.svet.utils.Utils

/**
 * Команда контроллера для вывода случайного цвета.
 **/
class RandomColor : Command {

    private val captureConfig = SvetConfig.captureConfig()

    override fun name(): String {
        return "Random Color"
    }

    override fun buffer(): ByteArray {
        return Utils.preparerRandomBuffer(captureConfig)
    }
}
