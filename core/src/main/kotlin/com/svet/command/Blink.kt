package com.svet.command

import com.svet.utils.Utils
import java.awt.Color

/**
 * Команда контроллера для вывода эффекта мерцания.
 **/
class Blink : Command {

    private var fon = Color(0, 0, 0)
    private var i = 0

    override fun name(): String {
        return "Blink"
    }

    override fun buffer(): ByteArray {
        val buffer = Utils.showSolidColorCmd(fon, false).toByteArray()

        fon = Color(i, i, i)
        i = (i + 5) % 250

        return buffer
    }
}
