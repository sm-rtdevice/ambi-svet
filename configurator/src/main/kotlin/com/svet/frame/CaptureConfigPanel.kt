package com.svet.frame

import com.svet.capture.CaptureScreen
import com.svet.config.SvetConfig
import com.svet.processor.ImageProcessorUtils
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.Timer

class CaptureConfigPanel internal constructor() : JPanel(), ActionListener {

    private val captureConfig = SvetConfig.captureConfig()
    private val verticalFontAlignCorrection = 3
    private val captureScreen = CaptureScreen()
    val timer: Timer = Timer(16, this)
    private var fpsTime = System.currentTimeMillis()

    init {
        this.preferredSize = Dimension(640, 480)
        timer.start()
    }

    override fun actionPerformed(e: ActionEvent?) {
        if (e?.source == timer) {
            repaint()
        }
    }

    override fun paint(graphics: Graphics) {
        val repaintTime = System.currentTimeMillis()

        val g2D = graphics as Graphics2D

        g2D.paint = Color.RED
        drawGridLines(g2D)

        g2D.paint = Color.BLACK
        drawCaptureColoredRegions(g2D)

        val elapsedTime = System.currentTimeMillis() - repaintTime
        print("\rrepaint time: $elapsedTime ms. FPS: ${1000L / (System.currentTimeMillis() - fpsTime)}")

        fpsTime = System.currentTimeMillis()
    }

    private fun drawGridLines(g2D: Graphics2D) {
        val x = captureConfig.width / 2
        val y = captureConfig.height / 2
        g2D.drawLine(x, 0, x, captureConfig.height)
        g2D.drawLine(0, y, captureConfig.width, y)
    }

    private fun drawCaptureColoredRegion(
        g2D: Graphics2D, colors: List<Color>, offset: Int, areaNumLeds: Int,
        ox: Int, oy: Int,
        lx1: Int, ly1: Int, lx2: Int, ly2: Int
    ) {
        for (i in 0 until areaNumLeds) {

            val ledNumber = i + offset

            // вокруг области захвата
            g2D.paint = Color.RED
            g2D.drawRect(
                captureConfig.positions[ledNumber].x - captureConfig.border,
                captureConfig.positions[ledNumber].y - captureConfig.border,
                captureConfig.captureRegionWidth + captureConfig.border,
                captureConfig.captureRegionHeight + captureConfig.border
            )

            // вокруг усредненного цвета области захвата
            g2D.paint = Color.green
            g2D.drawRect(
                captureConfig.positions[ledNumber].x - captureConfig.border + ox,
                captureConfig.positions[ledNumber].y - captureConfig.border + oy,
                captureConfig.captureRegionWidth + captureConfig.border,
                captureConfig.captureRegionHeight + captureConfig.border
            )

            // усредненный цвет
            g2D.drawImage(
                ImageProcessorUtils.createMonotonousImage(
                    captureConfig.captureRegionWidth,
                    captureConfig.captureRegionHeight,
                    BufferedImage.TYPE_3BYTE_BGR,
                    colors[ledNumber]
                ),
                captureConfig.positions[ledNumber].x + ox,
                captureConfig.positions[ledNumber].y + oy,
                this
            )

            // номер области захвата
            val ledNumberStr = (ledNumber).toString()
            graphics.drawString(
                ledNumberStr,
                captureConfig.positions[ledNumber].x - captureConfig.border + ox + captureConfig.captureRegionWidth / 2 - graphics.fontMetrics.stringWidth(ledNumberStr) / 2,
                captureConfig.positions[ledNumber].y - captureConfig.border + oy + captureConfig.captureRegionHeight / 2 + verticalFontAlignCorrection
            )

            // соединительная линия
            g2D.paint = Color.YELLOW
            g2D.drawLine(
                captureConfig.positions[ledNumber].x + lx1,
                captureConfig.positions[ledNumber].y + ly1,
                captureConfig.positions[ledNumber].x + lx2,
                captureConfig.positions[ledNumber].y + ly2
            )
        }
    }

    private fun drawCaptureColoredRegions(g2D: Graphics2D) {
        val colors = captureScreen.getRegionsCaptureColors(captureScreen.capture(), captureConfig)

        g2D.stroke = BasicStroke(captureConfig.border.toFloat())

        val ox = captureConfig.captureRegionWidth + captureConfig.section
        val oy = captureConfig.captureRegionHeight + captureConfig.section

        val vLineX = captureConfig.captureRegionWidth / 2
        val hLineY = captureConfig.captureRegionHeight / 2

        // области захвата снизу слева
        var offset = 0
        drawCaptureColoredRegion(
            g2D, colors, offset, captureConfig.bottomCountL,
            0, oy * (-1),
            vLineX, captureConfig.border * (-1), vLineX, captureConfig.section * (-1)
        )

        // области захвата слева
        offset += captureConfig.bottomCountL
        drawCaptureColoredRegion(
            g2D, colors, offset, captureConfig.leftCount,
            ox, 0,
            captureConfig.captureRegionWidth, hLineY, captureConfig.captureRegionWidth + captureConfig.section - captureConfig.border, hLineY
        )

        // области захвата сверху
        offset += captureConfig.leftCount
        drawCaptureColoredRegion(
            g2D, colors, offset, captureConfig.topCount,
            0, oy,
            vLineX, captureConfig.captureRegionHeight, vLineX, captureConfig.section + captureConfig.captureRegionHeight - captureConfig.border
        )

        // области захвата справа
        offset += captureConfig.topCount
        drawCaptureColoredRegion(
            g2D, colors, offset, captureConfig.rightCount,
            ox * (-1), 0,
            (captureConfig.border) * (-1), hLineY, captureConfig.section * (-1), hLineY
        )

        // области захвата снизу справа
        offset += captureConfig.rightCount
        drawCaptureColoredRegion(
            g2D, colors, offset, captureConfig.bottomCountR, 0, oy * (-1),
            vLineX, captureConfig.border * (-1), vLineX, captureConfig.section * (-1)
        )
    }

}
