package com.svet.enums

enum class ProgramMode(val mode: Int) {
    CAPTURE(0), // default mode

    SET_SOLID_COLOR(1),
    SET_STARTUP_MODE(2),

    INIT(3),
    CONNECT(4),
    RECONNECT(5),
    DISCONNECT(6),

    EXIT_PROGRAM(7),

    RANDOM_SCENE(8),
    BLINK(9),
    GRADIENT(10),
    TEST_LEDS(11),
    TEST_MODE(999);

    companion object {
        infix fun from(value: Int?): ProgramMode = entries.firstOrNull { it.mode == value } ?: CAPTURE
    }
}
