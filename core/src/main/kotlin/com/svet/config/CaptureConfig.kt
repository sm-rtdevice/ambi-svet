package com.svet.config

import com.svet.enums.OutputDirection
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

/**
 * Конфигурация захвата экрана.
 **/
class CaptureConfig {

    // количество зон:
    // сверху 32
    // по вертикали 20
    // вырез снизу 6

    var topCount: Int = 32
    var leftCount: Int = 18
    var rightCount: Int = 18
    var bottomCountR: Int = 13
    var bottomCountL: Int = 13

    // всего LED: 94
    // ledsCount = topCount + leftCount + rightCount + bottomCountR + bottomCountL
    var ledsCount = 94
    var initialCapacity = ledsCount * 3 + 3 + 3
    var outputDirection: OutputDirection = OutputDirection.CLOCKWISE

    // device - устройство захвата
    // Rectangle - размеры экрана (в пикселях)
    var width: Int = 1920
    var height: Int = 1080

    // высота и ширина области захвата (в пикселях) для получения усредненного цвета
    var captureRegionWidth: Int = 50
    var captureRegionHeight: Int = 50

    // вырез снизу по центру (в пикселях)
    var bottomCutout: Int = 200

    // координаты областей захвата
    var positions: MutableList<Position> = ArrayList(ledsCount) // 94 num of LED's

    // длина отрезка выносной линии
    var section: Int = 20

    // ширина рамки
    var border: Int = 1

    private fun defaultConfig() {
        outputDirection = OutputDirection.CLOCKWISE

        val hArea = width / 2 - bottomCutout / 2

        // области захвата снизу слева
        var horizontalOffset = 0
        for (i in 0 until bottomCountL) {
            val segment = hArea / bottomCountL
            val x = width/2 /* - borderV*/ - segment - bottomCutout/2 + (segment/2 - captureRegionWidth/2 - horizontalOffset) // <-- отступ справа
            horizontalOffset += segment
            val y = height - captureRegionHeight
            positions.add(Position(x, y))
        }

        // области захвата слева
        var vArea = (height - captureRegionHeight * 2) /* - borderH )*/ / leftCount
//        var verticalOffset = captureRegionHeight
//        for (i in 0 until leftCount) {
//            val y = verticalOffset + (vArea / 2 - captureRegionHeight / 2)
//            verticalOffset += vArea
//            //val x = width - captureRegionWidth /* - borderV*/ // <-- отступ справа
//            positions.add(Position(0, y))
//        }
        var verticalOffset = vArea * leftCount
        for (i in 0 until leftCount) {
            val y = verticalOffset + (vArea / 2 - captureRegionHeight / 2)
            verticalOffset -= vArea
            positions.add(Position(0, y))
        }

        // области захвата сверху
        horizontalOffset = 0
        for (i in 0 until topCount) {
            val x = width / topCount / 2 - captureRegionWidth / 2 + horizontalOffset
            horizontalOffset += width / topCount
            positions.add(Position(x, 0)) // y + borderH
        }

        // области захвата справа
        vArea = (height - captureRegionHeight * 2) /* - borderH )*/ / rightCount
        verticalOffset = vArea
        for (i in 0 until rightCount) {
            val y = verticalOffset + (vArea / 2 - captureRegionHeight / 2)
            verticalOffset += vArea
            val x = width - captureRegionWidth /* - borderV*/ // <-- отступ справа
            positions.add(Position(x, y))
        }

        // области захвата снизу справа
        horizontalOffset = 0
        for (i in 0 until bottomCountR) {
            val segment = hArea / bottomCountR
            val x = width /* - borderV*/ - segment + (segment/2 - captureRegionWidth/2 - horizontalOffset) // <-- отступ справа
            horizontalOffset += segment
            val y = height - captureRegionHeight
            positions.add(Position(x, y))
        }
    }

    data class Position(
        var x: Int = 0,
        var y: Int = 0
    )

    companion object {
        fun defaultConfig(): CaptureConfig {
            log.debug { "Create default configuration" }
            val result = CaptureConfig()
            result.defaultConfig()
            return result
        }
    }

}
