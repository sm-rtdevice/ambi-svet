package com.svet.command

import com.svet.capture.CaptureScreen
import com.svet.config.SvetConfig

/**
 * Команда контроллера для вывода усреднённых цветов областей захвата.
 **/
class Capture : Command {

    private val captureScreen = CaptureScreen()
    private val captureConfig = SvetConfig.captureConfig()

    override fun name(): String {
        return "Capture"
    }

    override fun buffer(): ByteArray {
        val regionsCaptureColors = captureScreen.getRegionsCaptureColors(captureScreen.capture(), captureConfig)
        return captureScreen.updateAdaBuffer(regionsCaptureColors, captureConfig).toByteArray()
    }
}
