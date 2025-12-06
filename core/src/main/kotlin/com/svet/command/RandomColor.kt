package com.svet.command

import com.svet.cmd.AdaPacket
import com.svet.config.SvetConfig

/**
 * Команда контроллера для вывода случайного цвета.
 **/
class RandomColor : Command {

    private val captureConfig = SvetConfig.captureConfig()

    override fun name(): String {
        return "Random Color"
    }

    override fun buffer(): ByteArray {
        return AdaPacket.showRandomColors(captureConfig)
    }
}
