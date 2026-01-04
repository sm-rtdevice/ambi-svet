package com.svet.processor

import com.svet.capture.AudioState
import com.svet.capture.FilterConst.AGC_ATTACK
import com.svet.capture.FilterConst.AGC_MAX_GAIN_DB
import com.svet.capture.FilterConst.AGC_MIN_GAIN_DB
import com.svet.capture.FilterConst.AGC_RELEASE
import com.svet.capture.FilterConst.AGC_TARGET_DB
import com.svet.capture.FilterConst.EMA_ATTACK
import com.svet.capture.FilterConst.EMA_RELEASE
import com.svet.capture.FilterConst.GATE_ATTACK
import com.svet.capture.FilterConst.GATE_RANGE_DB
import com.svet.capture.FilterConst.GATE_RELEASE
import com.svet.capture.FilterConst.GATE_THRESHOLD_DB
import com.svet.capture.FilterConst.MAX_DB
import com.svet.capture.FilterConst.MIN_DB
import com.svet.capture.FilterConst.OUTPUT_MAX
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

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

    // Перевод входящего сигналя в децибелы (dB)
    fun toDecibels(samples: DoubleArray): Double {
        var sumSq = 0.0
        for (sample in samples) {
            sumSq += sample * sample
        }
        val rms = sqrt(sumSq / samples.size) // RMS (Root Mean Square)
        val decibels = 20 * log10(rms + 1e-9)
        return decibels
    }

    // Automatic Gain Control (AGC) — автоматическое регулирование усиления
    fun automaticGainControl(inputDb: Double, state: AudioState): Double {
        val diff = AGC_TARGET_DB - inputDb
        val speed = if (diff < state.agc.gainDb) AGC_ATTACK else AGC_RELEASE // val speed = if (diff > 0) AGC_ATTACK else AGC_RELEASE
        state.agc.gainDb += (diff - state.agc.gainDb) * speed
        state.agc.gainDb = state.agc.gainDb.coerceIn(AGC_MIN_GAIN_DB, AGC_MAX_GAIN_DB)
        return 10.0.pow(state.agc.gainDb / 20.0)
    }

    // Шумоподавление
    fun noiseGate(inputDb: Double, state: AudioState): Double {
        val gateTargetDb = if (inputDb < GATE_THRESHOLD_DB) -((GATE_THRESHOLD_DB - inputDb).coerceAtMost(GATE_RANGE_DB)) else 0.0
        val gateSpeed = if (gateTargetDb < state.gate.attenuationDb) GATE_ATTACK else GATE_RELEASE
        state.gate.attenuationDb += (gateTargetDb - state.gate.attenuationDb) * gateSpeed
        return 10.0.pow(state.gate.attenuationDb / 20.0)
    }

    fun normalization(frequencies: IntArray) {
        for (i in frequencies.indices) {
            val clampedDb = frequencies[i].toDouble().coerceIn(MIN_DB, MAX_DB)
            frequencies[i] = (((clampedDb - MIN_DB) / (MAX_DB - MIN_DB) * OUTPUT_MAX)).toInt()
        }
    }

    // EMA — экспоненциальне скользящее среднее
    fun exponentialMovingAverage(frequencies: IntArray, state: AudioState) {
        for (i in frequencies.indices) {
            val alpha = if (frequencies[i] > state.ema.values[i]) EMA_ATTACK else EMA_RELEASE
            state.ema.values[i] = alpha * frequencies[i] + (1 - alpha) * state.ema.values[i]
            frequencies[i] = state.ema.values[i].toInt()
        }
    }
}
