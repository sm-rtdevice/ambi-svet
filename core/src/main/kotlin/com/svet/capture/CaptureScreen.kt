package com.svet.capture

import com.svet.config.CaptureConfig
import com.svet.config.SvetConfig
import com.svet.processor.ImageProcessorUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Color
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage

private val log = KotlinLogging.logger {}

/**
 * Захват снимака экрана, подготовка буфера для отправки в COM порт.
 */
class CaptureScreen {

    private val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
    private val robot = Robot()
    private val captureConfig = SvetConfig.captureConfig() // TODO: прокинуть через конструктор
    private val buffer = ByteArray(captureConfig.initialCapacity)

    init {
        buffer[0] = 'A'.code.toByte()
        buffer[1] = 'd'.code.toByte()
        buffer[2] = 'a'.code.toByte()
        buffer[3] = 0 // hi
        buffer[4] = 0 // lo
        buffer[5] = 0x55 // chk
        for (i in 6..buffer.lastIndex) {
            buffer[i] = 0
        }
    }

    /**
     * Захват снимака экрана.
     * @return снимок экрана
     */
    fun capture(): BufferedImage {
        return robot.createScreenCapture(screenRect)
    }

    /**
     * Определение усредненного цвета областей захвата.
     * @param capturedScreenshot захваченный снимок экрана
     * @param captureConfig конфигурация захвата
     * @return массив цветов для светодиодов
     */
    fun getRegionsCaptureColors(capturedScreenshot: BufferedImage, captureConfig: CaptureConfig): List<Color> {
        //TODO: check out of bounds
        if (capturedScreenshot.width != captureConfig.width || capturedScreenshot.height != captureConfig.height) {
            //reInitPositions: captureConfig.positions[] = ...
            log.error { "Change resolution size during work not supported yet" }
            throw IndexOutOfBoundsException("TODO: change resolution size during work")
        }

        val result = ArrayList<Color>(captureConfig.positions.size)

        for (i in captureConfig.positions.indices) {
            result.add(
                ImageProcessorUtils.getAverageColor(
                    capturedScreenshot.getSubimage(
                        captureConfig.positions[i].x,
                        captureConfig.positions[i].y,
                        captureConfig.captureRegionWidth,
                        captureConfig.captureRegionHeight
                    )
                )
            )
        }

        return result
    }

    /**
     * Обновление буфера контроллера.
     * @param regionCaptureColors массив цветов для светодиодов
     * @return обновлённый буфер
     */
    fun updateAdaBuffer(regionCaptureColors: List<Color>): ByteArray {
        for (i in 0 until regionCaptureColors.size) {
            buffer[6 + 3 * i] = regionCaptureColors[i].red.toByte()
            buffer[7 + 3 * i] = regionCaptureColors[i].green.toByte()
            buffer[8 + 3 * i] = regionCaptureColors[i].blue.toByte()
        }
        return buffer
    }

    fun updateAdaBufferAverageColorChannel(regionCaptureColors: List<Color>): ByteArray {
        for (i in 0 until regionCaptureColors.size) {
            buffer[6 + 3 * i] = ImageProcessorUtils.getAverageColorChannel(regionCaptureColors[i].red.toByte(), buffer[6 + 3 * i])
            buffer[7 + 3 * i] = ImageProcessorUtils.getAverageColorChannel(regionCaptureColors[i].green.toByte(), buffer[7 + 3 * i])
            buffer[8 + 3 * i] = ImageProcessorUtils.getAverageColorChannel(regionCaptureColors[i].blue.toByte(), buffer[8 + 3 * i])
        }
        return buffer
    }

    /**
     * Обновление буфера контроллера, устанавливает все светодиоды одним цветом.
     * @param color цвет для всех светодиодов
     * @param captureConfig конфигурация захвата
     * @return обновлённый буфер
     */
    fun updateAdaBuffer(color: Color, captureConfig: CaptureConfig): ByteArray {
        val capOffset = 6
        val step = 3
        for (i in 0 until captureConfig.ledsCount) {
            buffer[capOffset + step * i] = color.red.toByte()
            buffer[capOffset + step * i + 1] = color.green.toByte()
            buffer[capOffset + step * i + 2] = color.blue.toByte()
        }

        return buffer
    }

    /**
     * Захватывает снимок экрана.
     * @param captureConfig конфигурация захвата
     * @return обновлённый буфер для отправки в контроллер
     */
    fun getAdaBuffer(captureConfig: CaptureConfig): ByteArray {
        return updateAdaBuffer(getRegionsCaptureColors(capture(), captureConfig))
    }

}
