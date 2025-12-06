package com.svet.command

import com.svet.cmd.AdaPacket

/**
 * Команда контроллера для установки режима запуска контроллера.
 **/
class StartupMode(private val mode: Byte) : Command {

    override fun name(): String {
        return "Startup Mode"
    }

    override fun buffer(): ByteArray {
        return AdaPacket.setStartupMode(mode)
    }
}
