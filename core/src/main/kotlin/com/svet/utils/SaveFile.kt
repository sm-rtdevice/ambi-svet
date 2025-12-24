package com.svet.utils

import java.io.File
import java.io.IOException

object SaveFile {

    fun <T> saveArrayToFileWithCommas(array: T, filePath: String) {
        try {
            val file = File(filePath)
            file.bufferedWriter().use { writer ->
                val resultString = when (array) {
                    is ByteArray -> array.joinToString(",") { it.toString() }
                    is DoubleArray -> array.joinToString(",") { it.toString() }
                    else -> throw IllegalArgumentException("Unsupported array type: ${array?.let { it::class.simpleName } ?: "null"}")
                }
                writer.write(resultString)
            }
            println("Файл успешно сохранен по пути: $filePath")
        } catch (e: IOException) {
            println("Ошибка при сохранении файла: ${e.message}")
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }

    fun saveDoubleArrayToFileWithCommas(doubleArray: DoubleArray, filePath: String) {
        try {
            val file = File(filePath)
            file.bufferedWriter()
                .use { writer ->
                    doubleArray
                        .joinToString(",") { it.toString() }
                        .also { writer.write(it) }
                }
            println("file successfully saved: $filePath")
        } catch (e: IOException) {
            println("an error occurred during file saving: ${e.message}")
        }
    }

    fun saveByteArrayToBinFile(byteArray: ByteArray, filePath: String) {
        try {
            val file = File(filePath)
            file.writeBytes(byteArray)  // Записывает байтовый массив в файл
            println("Файл успешно сохранен по пути: $filePath")
        } catch (e: IOException) {
            println("Ошибка при сохранении файла: ${e.message}")
        }
    }

    fun saveDoubleArrayToBinFile(doubleArray: DoubleArray, filePath: String) {
        try {
            val file = File(filePath)
            file.bufferedWriter().use { writer ->
                doubleArray.forEach { writer.write("$it\n") }
            }
            println("Файл успешно сохранен по пути: $filePath")
        } catch (e: IOException) {
            println("Ошибка при сохранении файла: ${e.message}")
        }
    }

    fun saveByteArrayToFileWithCommas(byteArray: ByteArray, filePath: String) {
        try {
            val file = File(filePath)
            file.bufferedWriter()
                .use { writer ->
                    byteArray
                        .joinToString(",") { it.toString() }
                        .also { writer.write(it) }
                }
            println("file successfully saved: $filePath")
        } catch (e: IOException) {
            println("an error occurred during file saving: ${e.message}")
        }
    }

}
