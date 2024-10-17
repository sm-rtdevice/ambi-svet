package com.svet.command

interface Command {
    fun name(): String
    fun buffer(): ByteArray
}
