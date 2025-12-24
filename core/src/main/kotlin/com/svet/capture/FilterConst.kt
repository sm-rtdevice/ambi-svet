package com.svet.capture

object FilterConst {
    // RMS логарифмическая нормализация дб клиппинг + масштабирование
    // Энергия полосы → RMS
    // RMS → децибелы (dB)
    // Клиппинг диапазона (например -80..0 dB)
    // Масштабирование в 0..100 (или любой диапазон)
    const val SAMPLE_RATE = 44100.0
    const val MIN_DB = -80.0   // тишина
    const val MAX_DB = 0.0     // максимум
    const val OUTPUT_MAX = 100 // диапазон Int

    // AGC автоподстройка громкости
    // Считать общий уровень сигнала (RMS в dB)
    // Поддерживать плавающий gain
    // Медленно увеличивать gain, если тихо
    // Быстро уменьшать gain, если громко → attack / release
    const val AGC_TARGET_DB = -20.0   // целевой уровень
    const val AGC_MAX_GAIN_DB = 30.0  // усиление
    const val AGC_MIN_GAIN_DB = -20.0 // ослабление
    const val AGC_ATTACK = 0.2   // скорость реакции на громкий сигнал
    const val AGC_RELEASE = 0.01 // скорость реакции на тихий сигнал

    // EMA
    // smoothed = α * current + (1 − α) * previous
    const val EMA_ATTACK = 0.6   // когда уровень растёт
    const val EMA_RELEASE = 0.2  // когда уровень падает

    // Noise Gate
    // Если сигнал ниже порога: attenuation = (threshold - level) / range
    const val GATE_THRESHOLD_DB = -55.0  // порог шума
    const val GATE_RANGE_DB = 40.0       // глубина подавления
    const val GATE_ATTACK = 0.3
    const val GATE_RELEASE = 0.05
}
