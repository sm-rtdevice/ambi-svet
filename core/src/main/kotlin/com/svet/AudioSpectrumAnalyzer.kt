package com.svet

import com.svet.capture.CaptureSound

fun main() {
    val captureSound = CaptureSound()
    captureSound.run()
    readln()
    captureSound.stop()
}
