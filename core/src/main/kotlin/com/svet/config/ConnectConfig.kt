package com.svet.config

/**
 * Конфигурация подключения к контроллеру.
 **/
class ConnectConfig {
    var portNumber: String = "COM4"
    var detectPorts: Boolean = true
    var arduinoRebootTimeout = 1500L
}
