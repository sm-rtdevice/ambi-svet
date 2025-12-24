package com.svet.processor

import kotlin.math.cos

object AudioProcessor {

    fun pcm16ToDouble(
        buffer: ByteArray,
        samples: DoubleArray
    ) {
        for (i in 0 until buffer.size step 2) {
            val low = buffer[i].toInt() and 0xFF
            val high = buffer[i + 1].toInt()
            val value = (high shl 8) or low
            samples[i / 2] = value.toDouble()
        }
    }

    // Оконная функция Ханна
    fun initHannaWindow(size: Int): DoubleArray {
        val hanna = DoubleArray(size)
        for (i in 0 until size) {
            hanna[i] = 0.5 * (1 - cos(2 * Math.PI * i / (size - 1)))
        }
        return hanna
    }

    // Оконная функция Хэминга
    fun initHammingWindow(size: Int): DoubleArray {
        val hamming = DoubleArray(size)
        for (i in 0 until size) {
            hamming[i] = 0.54 - 0.46 * cos(2 * Math.PI * i / (size - 1))
        }
        return hamming
    }

    fun applyWindowFunc(windowFunc: DoubleArray, samples: DoubleArray) {
        for (i in 0 until samples.size) {
            samples[i] *= windowFunc[i]
        }
    }

    fun filterNoiseByFrequency(spectrum: IntArray, noiseThresholds: IntArray) {
        for (i in spectrum.indices) {
            if (spectrum[i] < noiseThresholds[i]) spectrum[i] = 0 else spectrum[i] -= noiseThresholds[i]
        }
    }

}
