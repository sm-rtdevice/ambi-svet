package com.svet.command

import com.svet.utils.Utils
import java.awt.Color

/**
 * Команда контроллера для вывода фонового (одинакового на всех светодиодах) цвета.
 **/
class SolidColor(private val color: Color, private val save: Boolean = false) : Command {

    override fun name(): String {
        return "Solid Color"
    }

    override fun buffer(): ByteArray {
        return Utils.showSolidColorCmd(color, save).toByteArray()
    }
}
