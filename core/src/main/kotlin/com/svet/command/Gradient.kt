package com.svet.command

import com.svet.processor.ImageProcessorUtils
import com.svet.utils.Utils
import java.awt.Color

/**
 * Команда контроллера для вывода градиента перехода между двумя цветами с заданной дискретностью.
 **/
class Gradient : Command {
    private val startColor = Color(0, 0, 255) // Начальный цвет (красный)
    private val endColor = Color(255, 0, 0) // Конечный цвет (синий)
    private val steps = 100 // Дискретность градиента
    private val colors = ImageProcessorUtils.calculateGradient(startColor, endColor, steps)

    private var i = 0
    private var direction = 1

    override fun name(): String {
        return "Gradient"
    }

    override fun buffer(): ByteArray {
        val buffer = Utils.showSolidColorCmd(colors[i], false).toByteArray()
        // в одном направлении
//        i = (i + direction) % (steps + 1)

        // в обоих направлениях
        i += direction
        if (i >= steps || i <= 0) {
            direction *= -1
        }

        return buffer
    }
}
