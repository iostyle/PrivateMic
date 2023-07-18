package com.iostyle.micphone

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

interface SoundMeterListener {
    fun onAmplitudeChanged(amplitude: Double)
}
@SuppressLint("MissingPermission")
class SoundMeter(private val listener: SoundMeterListener) : Runnable {
    private var isRunning = false
    private val audioRecord: AudioRecord
    private val buffer: ShortArray

    init {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        buffer = ShortArray(bufferSize)
    }

    override fun run() {
        isRunning = true
        audioRecord.startRecording()

        while (isRunning) {
            val bytesRead = audioRecord.read(buffer, 0, buffer.size)
            var sum = 0L

            for (i in 0 until bytesRead) {
                val sample = buffer[i].toLong()
                sum += sample * sample
            }

            if (bytesRead > 0) {
                // 在这里处理振幅（音量）的回调，可以将其发送到其他组件或显示在界面上
                // ...
                val amplitude = Math.sqrt(sum.toDouble() / bytesRead.toDouble())
                listener.onAmplitudeChanged(amplitude)
            }
        }

        audioRecord.stop()
        audioRecord.release()
    }

    fun start() {
        Thread(this).start()
    }

    fun stop() {
        isRunning = false
    }
}
