package com.svet.command

import com.svet.cmd.AdaPacket

/**
 * Команда контроллера для вывода случайного цвета.
 **/
class TestLeds(val ledsCount: Int) : Command {

    override fun name(): String {
        return "Test led's"
    }

    override fun buffer(): ByteArray {
        return AdaPacket.testLeds(ledsCount)
    }
}
