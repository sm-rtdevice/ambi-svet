package com.svet.config

/**
 * Конфигурация подключения к контроллеру.
 **/
class ConnectConfig {
    var portNumber: String = "COM5"
    var detectPorts: Boolean = true
    var arduinoRebootTimeout = 1500L
}
