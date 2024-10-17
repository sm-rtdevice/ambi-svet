package com.svet.capture

import com.svet.config.CaptureConfig
import com.svet.processor.ImageProcessorUtils
import java.awt.Color
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage

class CaptureScreen {

    private val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
    private val robot = Robot()
    private var buffer = ArrayList<Byte>(288)

    init {
        buffer.addAll(listOf('A'.code.toByte(), 'd'.code.toByte(), 'a'.code.toByte()))
        val hi: Byte = 0
        val lo: Byte = 0
        val chk: Byte = 0x55
        buffer.addAll(listOf(hi, lo, chk))
        for (i in 6 + 1..288) {
            buffer.add(0)
        }
    }

    fun capture(): BufferedImage {
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

    fun updateAdaBuffer(regionCaptureColors: List<Color>, captureConfig: CaptureConfig): List<Byte> {
//        buffer[0..287]
//        regionCaptureColors[0..93]
        val capOffset = 6
        val step = 3
        for (i in 0 until captureConfig.ledsCount) {
            buffer[capOffset + step * i] = regionCaptureColors[i].red.toByte()
            buffer[capOffset + step * i + 1] = regionCaptureColors[i].green.toByte()
            buffer[capOffset + step * i + 2] = regionCaptureColors[i].blue.toByte()
            // todo: test average color
//            buffer[capOffset + step * i] = getAverageColorChannel(regionCaptureColors[i].red.toByte(), buffer[capOffset + step * i])
//            buffer[capOffset + step * i + 1] = getAverageColorChannel(regionCaptureColors[i].green.toByte(), buffer[capOffset + step * i + 1])
//            buffer[capOffset + step * i + 2] = getAverageColorChannel(regionCaptureColors[i].blue.toByte(), buffer[capOffset + step * i + 2])
        }

        return buffer
    }

    //showSolidColor
    fun updateAdaBuffer(color: Color, captureConfig: CaptureConfig): List<Byte> {
        val capOffset = 6
        val step = 3
        for (i in 0 until captureConfig.ledsCount) {
            buffer[capOffset + step * i] = color.red.toByte()
            buffer[capOffset + step * i + 1] = color.green.toByte()
            buffer[capOffset + step * i + 2] = color.blue.toByte()
        }

        return buffer
    }

}
