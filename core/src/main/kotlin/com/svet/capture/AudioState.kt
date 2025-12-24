package com.svet.capture

class AgcState {
    var gainDb = 0.0
}

class EmaState(bandCount: Int) {
    val values = DoubleArray(bandCount) { 0.0 }
}

class NoiseGateState {
    var attenuationDb = 0.0
}

class AudioState(size: Int) {
    val agc = AgcState()
    val ema = EmaState(size)
    val gate = NoiseGateState()
}
