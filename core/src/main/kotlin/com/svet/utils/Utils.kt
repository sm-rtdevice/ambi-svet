package com.svet.utils

import kotlin.experimental.xor

object Utils {
    /**
     * Старший байт длины.
     *
     * @param dataLen длина передаваемых данных
     * @return старший байт длины
     **/
    fun getHi(dataLen: Int): Byte {
        return (dataLen shr 8).toByte() // dataLen >> 8
    }

    /**
     * Младший байт длины.
     *
     * @param dataLen длина передаваемых данных
     * @return младший байт длины
     **/
    fun getLo(dataLen: Int): Byte {
        return (dataLen and 0xFF).toByte() // dataLen & 0xFF
    }

    /**
     * Контрольный байт для проверки синхронизации пакетов.
     *
     * @param hi старший байт длины.
     * @param lo младший байт длины
     * @param salt константа
     * @return контрольный байт
     **/
    fun getChk(hi: Byte, lo: Byte, salt: Byte = 0x55): Byte {
        return (hi xor lo xor salt).toByte()
    }

    fun hiLoChk(dataLen: Int): Triple<Byte, Byte, Byte> {
        val hi = getHi(dataLen)
        val lo = getLo(dataLen)
        val chk = getChk(hi, lo)
        return Triple(hi, lo, chk)
    }
}
