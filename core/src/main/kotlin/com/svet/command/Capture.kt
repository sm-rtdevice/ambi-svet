package com.svet.command

import com.svet.capture.CaptureScreen
import com.svet.config.SvetConfig

/**
 * Команда контроллера для вывода усреднённых цветов областей захвата.
 **/
class Capture : Command {

    private val captureScreen = CaptureScreen() // todo:
    private val svetConfig = SvetConfig() // todo: singleton только svetConfig.captureConfig

    override fun name(): String {
        return "Capture"
    }

    override fun buffer(): ByteArray {
        val captureConfig = svetConfig.captureConfig
        val regionsCaptureColors = captureScreen.getRegionsCaptureColors(captureScreen.capture(), captureConfig)
        return captureScreen.updateAdaBuffer(regionsCaptureColors, captureConfig).toByteArray()
    }
}
