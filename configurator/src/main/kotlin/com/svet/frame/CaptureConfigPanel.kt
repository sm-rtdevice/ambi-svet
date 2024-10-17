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

    private val svetConfig = SvetConfig()
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
        val x = svetConfig.captureConfig.width / 2
        val y = svetConfig.captureConfig.height / 2
        g2D.drawLine(x, 0, x, svetConfig.captureConfig.height)
        g2D.drawLine(0, y, svetConfig.captureConfig.width, y)
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
                svetConfig.captureConfig.positions[ledNumber].x - svetConfig.captureConfig.border,
                svetConfig.captureConfig.positions[ledNumber].y - svetConfig.captureConfig.border,
                svetConfig.captureConfig.captureRegionWidth + svetConfig.captureConfig.border,
                svetConfig.captureConfig.captureRegionHeight + svetConfig.captureConfig.border
            )

            // вокруг усредненного цвета области захвата
            g2D.paint = Color.green
            g2D.drawRect(
                svetConfig.captureConfig.positions[ledNumber].x - svetConfig.captureConfig.border + ox,
                svetConfig.captureConfig.positions[ledNumber].y - svetConfig.captureConfig.border + oy,
                svetConfig.captureConfig.captureRegionWidth + svetConfig.captureConfig.border,
                svetConfig.captureConfig.captureRegionHeight + svetConfig.captureConfig.border
            )

            // усредненный цвет
            g2D.drawImage(
                ImageProcessorUtils.createMonotonousImage(
                    svetConfig.captureConfig.captureRegionWidth,
                    svetConfig.captureConfig.captureRegionHeight,
                    BufferedImage.TYPE_3BYTE_BGR,
                    colors[ledNumber]
                ),
                svetConfig.captureConfig.positions[ledNumber].x + ox,
                svetConfig.captureConfig.positions[ledNumber].y + oy,
                this
            )

            // номер области захвата
            val ledNumberStr = (ledNumber).toString()
            graphics.drawString(
                ledNumberStr,
                svetConfig.captureConfig.positions[ledNumber].x - svetConfig.captureConfig.border + ox + svetConfig.captureConfig.captureRegionWidth / 2 - graphics.fontMetrics.stringWidth(ledNumberStr) / 2,
                svetConfig.captureConfig.positions[ledNumber].y - svetConfig.captureConfig.border + oy + svetConfig.captureConfig.captureRegionHeight / 2 + verticalFontAlignCorrection
            )

            // соединительная линия
            g2D.paint = Color.YELLOW
            g2D.drawLine(
                svetConfig.captureConfig.positions[ledNumber].x + lx1,
                svetConfig.captureConfig.positions[ledNumber].y + ly1,
                svetConfig.captureConfig.positions[ledNumber].x + lx2,
                svetConfig.captureConfig.positions[ledNumber].y + ly2
            )
        }
    }

    private fun drawCaptureColoredRegions(g2D: Graphics2D) {
        val colors = captureScreen.getRegionsCaptureColors(captureScreen.capture(), svetConfig.captureConfig)

        g2D.stroke = BasicStroke(svetConfig.captureConfig.border.toFloat())

        val ox = svetConfig.captureConfig.captureRegionWidth + svetConfig.captureConfig.section
        val oy = svetConfig.captureConfig.captureRegionHeight + svetConfig.captureConfig.section

        val vLineX = svetConfig.captureConfig.captureRegionWidth / 2
        val hLineY = svetConfig.captureConfig.captureRegionHeight / 2

        // области захвата снизу слева
        var offset = 0
        drawCaptureColoredRegion(
            g2D, colors, offset, svetConfig.captureConfig.bottomCountL,
            0, oy * (-1),
            vLineX, svetConfig.captureConfig.border * (-1), vLineX, svetConfig.captureConfig.section * (-1)
        )

        // области захвата слева
        offset += svetConfig.captureConfig.bottomCountL
        drawCaptureColoredRegion(
            g2D, colors, offset, svetConfig.captureConfig.leftCount,
            ox, 0,
            svetConfig.captureConfig.captureRegionWidth, hLineY, svetConfig.captureConfig.captureRegionWidth + svetConfig.captureConfig.section - svetConfig.captureConfig.border, hLineY
        )

        // области захвата сверху
        offset += svetConfig.captureConfig.leftCount
        drawCaptureColoredRegion(
            g2D, colors, offset, svetConfig.captureConfig.topCount,
            0, oy,
            vLineX, svetConfig.captureConfig.captureRegionHeight, vLineX, svetConfig.captureConfig.section + svetConfig.captureConfig.captureRegionHeight - svetConfig.captureConfig.border
        )

        // области захвата справа
        offset += svetConfig.captureConfig.topCount
        drawCaptureColoredRegion(
            g2D, colors, offset, svetConfig.captureConfig.rightCount,
            ox * (-1), 0,
            (svetConfig.captureConfig.border) * (-1), hLineY, svetConfig.captureConfig.section * (-1), hLineY
        )

        // области захвата снизу справа
        offset += svetConfig.captureConfig.rightCount
        drawCaptureColoredRegion(
            g2D, colors, offset, svetConfig.captureConfig.bottomCountR, 0, oy * (-1),
            vLineX, svetConfig.captureConfig.border * (-1), vLineX, svetConfig.captureConfig.section * (-1)
        )
    }

}
