package com.svet.enums

/**
 * Команды arduino-контроллера.
 */
enum class ArduinoCommands(val cmd: Byte) {
    SHOW_ARRAY_COLOR_CMD(0), // управление каждым по отдельности (для режима захвата)
    SHOW_SOLID_COLOR_CMD(1), // сплошной цвет на всех диодах
    SET_STARTUP_MODE_CMD(2)  // управление режимом при включении контроллера
}
