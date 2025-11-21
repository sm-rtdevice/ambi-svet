package com.svet.utils

import com.svet.config.CaptureConfig
import com.svet.enums.ArduinoCommands
import java.awt.Color

object Utils {
    /**
     * Вывод случайного цвета на все светодиоды.
     *
     * @param captureConfig конфигурация захвата экрана
     * @return массив байтов для контроллера
     **/
    fun preparerRandomBuffer(captureConfig: CaptureConfig): List<Byte> {
        val buffer = ArrayList<Byte>(captureConfig.initialCapacity)

        val bright: Byte = 100 // 1..127: 1 - max яркость, 127 - min яркость

        val hi: Byte = 0
        val lo: Byte = 0
        val chk: Byte = 0x55

        buffer.addAll(listOf('A'.code.toByte(), 'd'.code.toByte(), 'a'.code.toByte())) // заголовок
        buffer.addAll(listOf(hi, lo, chk)) // CRC?

        for (i in 1..captureConfig.ledsCount) {
            buffer.addAll(
                listOf(
//            (-128..127).random().toByte(),  // R
//            (-128..127).random().toByte(),  // G
//            (-128..127).random().toByte())) // B
                    (0..127 / bright).random().toByte(),  // R
                    (0..127 / bright).random().toByte(),  // G
                    (0..127 / bright).random().toByte())) // B
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
    fun showSolidColorCmd(color: Color, save: Boolean = false): List<Byte> {
        val hi: Byte = 0
        val lo: Byte = 0
        val chk: Byte = 0x55

        return listOf(
            'c'.code.toByte(), 'm'.code.toByte(), 'd'.code.toByte(),
            hi, lo, chk, // CRC?
            ArduinoCommands.SHOW_SOLID_COLOR_CMD.cmd,
            color.red.toByte(),
            color.green.toByte(),
            color.blue.toByte(),
            if (save) 1 else 0
        )
    }

    /**
     * Установить режим отображения при включении контроллера.
     *
     * @param mode режим отображения: 1 - отображать сохранённый цвет при включении контроллера, 0 - не отображать
     * @return массив байтов для контроллера
     **/
    fun setStartupModeCmd(mode: Byte): List<Byte> {
        val hi: Byte = 0
        val lo: Byte = 0
        val chk: Byte = 0x55

        return listOf(
            'c'.code.toByte(), 'm'.code.toByte(), 'd'.code.toByte(),
            hi, lo, chk, // CRC?
            ArduinoCommands.SET_STARTUP_MODE_CMD.cmd,
            mode
        )
    }

}
