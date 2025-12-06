package com.svet.cmd

import com.svet.config.CaptureConfig
import com.svet.enums.ArduinoCommands
import com.svet.utils.Utils.hiLoChk
import java.awt.Color

object AdaPacket {
    /**
     * Вывод случайного цвета на все светодиоды.
     *
     * @param captureConfig конфигурация захвата экрана
     * @return массив байтов для контроллера
     **/
    fun showRandomColors(captureConfig: CaptureConfig): ByteArray {
        val brightness: Byte = 100 // 1..127: 1 - max яркость, 127 - min яркость
        val buffer = ByteArray(captureConfig.initialCapacity)
        val (hi, lo, chk) = hiLoChk(captureConfig.dataLen())
        buffer[0] = 'A'.code.toByte()
        buffer[1] = 'd'.code.toByte()
        buffer[2] = 'a'.code.toByte()
        buffer[3] = hi
        buffer[4] = lo
        buffer[5] = chk

        for (i in 0 until captureConfig.ledsCount) {
            buffer[6 + 3 * i] = (0..127 / brightness).random().toByte() // R
            buffer[7 + 3 * i] = (0..127 / brightness).random().toByte() // G
            buffer[8 + 3 * i] = (0..127 / brightness).random().toByte() // B
        }

        return buffer
    }

    /**
     * Вывод сплошного цвета на все светодиоды.
     *
     * @param color выводимый цвет
     * @param save true - сохранить, false - не сохранять цвет в eeprom контроллера
     * @return массив байтов для контроллера
     **/
    fun showSolidColor(color: Color, save: Boolean = false): ByteArray {
        val dataLen = 5
        val buffer = ByteArray(6 + dataLen)
        val (hi, lo, chk) = hiLoChk(dataLen)
        buffer[0] = 'c'.code.toByte()
        buffer[1] = 'm'.code.toByte()
        buffer[2] = 'd'.code.toByte()
        buffer[3] = hi
        buffer[4] = lo
        buffer[5] = chk
        buffer[6] = ArduinoCommands.SHOW_SOLID_COLOR_CMD.cmd
        buffer[7] = color.red.toByte()
        buffer[8] = color.green.toByte()
        buffer[9] = color.blue.toByte()
        buffer[10] = if (save) 1 else 0
        return buffer
    }

    /**
     * Установить режим отображения при включении контроллера.
     *
     * @param mode режим отображения: 1 - отображать сохранённый цвет при включении контроллера, 0 - не отображать
     * @return массив байтов для контроллера
     **/
    fun setStartupMode(mode: Byte): ByteArray {
        val dataLen = 2
        val buffer = ByteArray(6 + dataLen)
        val (hi, lo, chk) = hiLoChk(dataLen)
        buffer[0] = 'c'.code.toByte()
        buffer[1] = 'm'.code.toByte()
        buffer[2] = 'd'.code.toByte()
        buffer[3] = hi
        buffer[4] = lo
        buffer[5] = chk
        buffer[6] = ArduinoCommands.SET_STARTUP_MODE_CMD.cmd
        buffer[7] = mode
        return buffer
    }
}
