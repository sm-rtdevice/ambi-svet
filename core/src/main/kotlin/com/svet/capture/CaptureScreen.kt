package com.svet.capture

import com.svet.config.CaptureConfig
import com.svet.processor.ImageProcessorUtils
import mu.KotlinLogging
import java.awt.*
import java.awt.image.BufferedImage
import java.io.IOException

private val logger = KotlinLogging.logger {}

class CaptureScreen {

    private val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
    private val robot = Robot()

    @Throws(AWTException::class, IOException::class)
    fun capture() : BufferedImage {
        return robot.createScreenCapture(screenRect)
    }

    // определение усредненного цвета областей захвата
    fun getRegionsCaptureColors(capturedScreenshot: BufferedImage, captureConfig: CaptureConfig): List<Color> {

        //TODO: check out of bounds

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

    fun toAdaBuffer(regionCaptureColors: List<Color>, captureConfig: CaptureConfig): List<Byte> {

        val buffer = ArrayList<Byte>(captureConfig.initialCapacity)

        val hi: Byte = 0
        val lo: Byte = 0
        val chk: Byte = 0x55

        buffer.addAll(listOf('A'.code.toByte(), 'd'.code.toByte(), 'a'.code.toByte())) // заголовок
        buffer.addAll(listOf(hi, lo, chk)) // CRC?

        for (i in regionCaptureColors.indices step 3) {
            buffer[i] = regionCaptureColors[i].red.toByte()
            buffer[i + 1] = regionCaptureColors[i + 1].green.toByte()
            buffer[i + 2] = regionCaptureColors[i + 2].blue.toByte()
        }

        return buffer
    }

}