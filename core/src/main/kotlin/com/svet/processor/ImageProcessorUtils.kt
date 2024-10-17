package com.svet.processor

import com.svet.enums.ImageFormat
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ImageProcessorUtils {

    fun load(fileName: String): BufferedImage {
        return ImageIO.read(File(fileName))
    }

    fun saveJpg(image: BufferedImage, fileName: String) {
        val output = File(fileName)
        ImageIO.write(image, "jpg", output)
    }

    fun save(image: BufferedImage, fileName: String, imageFormat: ImageFormat) {
        val output = File(fileName)
        ImageIO.write(image, imageFormat.format, output)
    }

    fun toGray(source: BufferedImage): BufferedImage {
        val result = BufferedImage(source.width, source.height, source.type)

        for (x in 0 until source.width) {
            for (y in 0 until source.height) {

                val color = Color(source.getRGB(x, y))
                val blue = color.blue
                val red = color.red
                val green = color.green

                // алгоритм для получения изображения в оттенках серого
                val grey = (red * 0.299 + green * 0.587 + blue * 0.114).toInt()

                result.setRGB(x, y, Color(grey, grey, grey).rgb)
            }
        }

        return result
    }

    fun createMonotonousImage(width: Int, height: Int, imageType: Int, avgColor: Color): BufferedImage {
        val result = BufferedImage(width, height, imageType)

        for (x in 0 until width) {
            for (y in 0 until height) {
                result.setRGB(x, y, avgColor.rgb)
            }
        }
        return result
    }

    fun getAverageColor(source: BufferedImage): Color {
        val count = source.width * source.height

        var red = 0L
        var green = 0L
        var blue = 0L

        for (x in 0 until source.width) {
            for (y in 0 until source.height) {
                val color = Color(source.getRGB(x, y))
                red += color.red
                green += color.green
                blue += color.blue
            }
        }

        return Color((red / count).toInt(), (green / count).toInt(), (blue / count).toInt())
    }

    fun getAverageColorChannel(first: Byte, second: Byte): Byte {
        return ((first + second) / 2).toByte()
    }

    /**
     * Рассчет градиента перехода между цветами с заданной дискретностью.
     *
     * @param startColor начальный цвет
     * @param endColor конечный цвет
     * @param steps дискретность [1..255]
     * @return градиент перехода [steps + 1]
     */
    fun calculateGradient(startColor: Color, endColor: Color, steps: Int): List<Color> {
        val gradient = mutableListOf<Color>() // todo: replace List
        val stepSize = 1.0 / steps
        for (i in 0..steps) {
            val ratio = i * stepSize
            val red = (startColor.red * (1 - ratio) + endColor.red * ratio).toInt()
            val green = (startColor.green * (1 - ratio) + endColor.green * ratio).toInt()
            val blue = (startColor.blue * (1 - ratio) + endColor.blue * ratio).toInt()
            gradient.add(Color(red, green, blue))
        }

        return gradient
    }
}
