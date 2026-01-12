package com.svet.command

import com.svet.cmd.AdaPacket
import java.awt.Color

/**
 * Команда контроллера для вывода фонового (одинакового на всех светодиодах) цвета.
 **/
class SolidColor(private val color: Color, private val save: Boolean = false) : Command {

    override fun name(): String {
        return "Solid Color"
    }

    override fun buffer(): ByteArray {
        return AdaPacket.showSolidColor(color, save)
    }
}
