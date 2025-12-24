package com.svet.capture

import com.svet.capture.FilterConst.AGC_ATTACK
import com.svet.capture.FilterConst.AGC_MAX_GAIN_DB
import com.svet.capture.FilterConst.AGC_MIN_GAIN_DB
import com.svet.capture.FilterConst.AGC_RELEASE
import com.svet.capture.FilterConst.AGC_TARGET_DB
import com.svet.capture.FilterConst.GATE_ATTACK
import com.svet.capture.FilterConst.GATE_RANGE_DB
import com.svet.capture.FilterConst.GATE_RELEASE
import com.svet.capture.FilterConst.GATE_THRESHOLD_DB
import com.svet.capture.FilterConst.SAMPLE_RATE
import com.svet.processor.AudioProcessor
import org.jtransforms.fft.DoubleFFT_1D
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.math.*

// частотные полосы, мкф 50-16000
val BANDS = intArrayOf(
    63, 125, 250, 500, 750, 1000, 1500, 2000, 3000, 4000, 6000, 8000, 11000, 14000, 17000, 20000
//    63, 90, 125, 180, 250, 350, 500, 700, 1000, 1400, 2000, 2800, 4000, 6000, 9000, 16000 // низкие и средние частоты
//    63, 100, 160, 250, 350, 500, 700, 1000, 1400, 2000, 2800, 4000, 5500, 8000, 11000, 16000 // вокал, с очень плотным охватом низа-середины и разборчивой верхней серединой
//    63, 100, 150, 200, 250, 350, 500, 700, 1000, 1400, 2000, 2800, 4000, 6000, 8000, 12000 // рок
)

val FREQUENCY_NOISE_LEVELS = intArrayOf(
    50, 50, 47, 45, 40, 36, 33, 28, 23, 22, 19, 17, 16, 14, 13, 12
)

class CaptureSound {
    val sampleRate = 44100f
    val bufferSize = 2048 // должен быть степенью 2 для FFT

    val format = AudioFormat(
        sampleRate,
        16,
        1,
        true,
        false
    )

    val info = DataLine.Info(TargetDataLine::class.java, format)
    val mic = AudioSystem.getLine(info) as TargetDataLine

    val buffer = ByteArray(bufferSize)
    val samples = DoubleArray(bufferSize / 2)
    val frequencies = IntArray(BANDS.size) { 0 }

    val windowFunc = AudioProcessor.initHammingWindow(bufferSize / 2)
    val state = AudioState(BANDS.size)

    var thread: Thread? = null

    init {
        mic.open(format, bufferSize)
        mic.start()
    }

    fun run() {
        thread = Thread {
            while (!Thread.interrupted()) {
                val bytesRead = mic.read(buffer, 0, buffer.size)
                if (bytesRead < bufferSize) continue

                AudioProcessor.pcm16ToDouble(buffer, samples)
                AudioProcessor.applyWindowFunc(windowFunc, samples)
                analogToFrequencies(samples, state, frequencies)
                AudioProcessor.filterNoiseByFrequency(frequencies, FREQUENCY_NOISE_LEVELS)

//                eqFrequency(BANDS, frequency)
                println(frequencies.joinToString(", ") { "%d".format(it) })
            }
        }

        thread?.start()
    }

    fun stop() {
        thread?.interrupt()
        mic.stop()
        mic.close()
    }

    private fun analogToFrequencies(
        analog: DoubleArray,
        state: AudioState,
        frequencies: IntArray
    ) {
        val n = analog.size

        // ---------- 1. RMS входа ----------
        var sumSq = 0.0
        for (v in analog) sumSq += v * v
        val rms = sqrt(sumSq / n)
        val inputDb = 20 * log10(rms + 1e-9)
//        println("RMS: $rms, inputDb: $inputDb")

        // ---------- 2. AGC ----------
        val diff = AGC_TARGET_DB - inputDb
        val speed = if (diff < state.agc.gainDb) AGC_ATTACK else AGC_RELEASE
        state.agc.gainDb += (diff - state.agc.gainDb) * speed
        state.agc.gainDb = state.agc.gainDb.coerceIn(AGC_MIN_GAIN_DB, AGC_MAX_GAIN_DB)
        val agcGain = 10.0.pow(state.agc.gainDb / 20.0)
//        println("AGC gain: ${state.agc.gainDb}")

        // ---------- 3. Noise Gate ----------
        val gateTargetDb = if (inputDb < GATE_THRESHOLD_DB) -((GATE_THRESHOLD_DB - inputDb).coerceAtMost(GATE_RANGE_DB)) else 0.0
        val gateSpeed = if (gateTargetDb < state.gate.attenuationDb) GATE_ATTACK else GATE_RELEASE
        state.gate.attenuationDb += (gateTargetDb - state.gate.attenuationDb) * gateSpeed
        val gateGain = 10.0.pow(state.gate.attenuationDb / 20.0)
//        println("agcGain: $agcGain gateGain: $gateGain")

        // ---------- 4. FFT ----------
        val fftData = DoubleArray(n * 2)
        for (i in 0 until n) {
            fftData[2 * i] = analog[i] * agcGain * gateGain
            fftData[2 * i + 1] = 0.0
        }

        val fft = DoubleFFT_1D(n.toLong())
        fft.complexForward(fftData)

        val magnitudes = DoubleArray(n / 2)
        for (i in 0 until n / 2) {
            val re = fftData[2 * i]
            val im = fftData[2 * i + 1]
            magnitudes[i] = sqrt(re * re + im * im)
        }

//        println("Magnitudes: ${magnitudes.joinToString(", ")}")

        // ---------- 5. Полосы + log + EMA ----------
        for (i in BANDS.indices) {
            val lowFreq = if (i == 0) 0 else BANDS[i - 1]
            val highFreq = BANDS[i]

            val lowIndex = ((lowFreq / SAMPLE_RATE) * n).toInt()
            val highIndex = ((highFreq / SAMPLE_RATE) * n).toInt()

            var bandSumSq = 0.0
            var count = 0

            for (k in lowIndex until min(highIndex, magnitudes.size)) {
                val v = magnitudes[k]
                bandSumSq += v * v
                count++
            }

            if (count == 0) continue

            val bandRms = sqrt(bandSumSq / count)
            val bandDb = 20 * log10(bandRms + 1e-9)
            frequencies[i] = bandDb.toInt()

//            val clampedDb = bandDb.coerceIn(MIN_DB, MAX_DB)
//
//            val normalized = ((clampedDb - MIN_DB) / (MAX_DB - MIN_DB) * OUTPUT_MAX)
//
//            // EMA
//            val prev = state.ema.values[i]
//            val alpha =
//                if (normalized > prev) EMA_ATTACK else EMA_RELEASE
//
//            val smoothed = alpha * normalized + (1 - alpha) * prev
//
//            state.ema.values[i] = smoothed
//            frequencies[i] = smoothed.toInt()
        }
    }

    fun eqFrequency(bands: IntArray, frequency: IntArray) {
        val sb = StringBuilder()
        for (i in bands.indices) {
            sb.append("%5d".format(bands[i]))
            sb.append(": ")
            sb.append("%d".format(frequency[i]))
            sb.append('\n')
        }
        println("\r\n$sb")
    }

}
