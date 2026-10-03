package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

class SoundFeedbackHelper {
    private val scope = CoroutineScope(Dispatchers.Default)

    // Precomputed short bead tap sound (wooden clack)
    private val beadTapTrackData: ShortArray by lazy {
        generateBeadClickBuffer()
    }

    // Precomputed singing bowl bell chime (432Hz harmonic)
    private val singingBowlData: ShortArray by lazy {
        generateSingingBowlBuffer()
    }

    fun playBeadSound(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            try {
                playAudioBuffer(beadTapTrackData, sampleRate = 22050)
            } catch (_: Exception) {
                // Fallback to ToneGenerator if AudioTrack unavailable
                try {
                    val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 30)
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
                    toneGen.release()
                } catch (_: Exception) {}
            }
        }
    }

    fun playMalaCompletedChime(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            try {
                playAudioBuffer(singingBowlData, sampleRate = 44100)
            } catch (_: Exception) {
                try {
                    val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
                    toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 350)
                    toneGen.release()
                } catch (_: Exception) {}
            }
        }
    }

    private fun playAudioBuffer(buffer: ShortArray, sampleRate: Int) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        // Allow playing and release after playback finishes
        Thread.sleep((buffer.size * 1000L) / sampleRate + 50)
        audioTrack.release()
    }

    private fun generateBeadClickBuffer(): ShortArray {
        val sampleRate = 22050
        val durationMs = 45
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        val freq = 820.0
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t * 85.0)
            val sample = sin(2.0 * Math.PI * freq * t) * decay * 0.45
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateSingingBowlBuffer(): ShortArray {
        val sampleRate = 44100
        val durationSeconds = 1.6
        val numSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)

        // Fundamental meditative 432Hz with gentle 864Hz overtone
        val f1 = 432.0
        val f2 = 864.0
        val f3 = 1296.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay1 = exp(-t * 2.2)
            val decay2 = exp(-t * 3.8)
            val decay3 = exp(-t * 5.5)

            val s1 = sin(2.0 * Math.PI * f1 * t) * decay1 * 0.65
            val s2 = sin(2.0 * Math.PI * f2 * t) * decay2 * 0.22
            val s3 = sin(2.0 * Math.PI * f3 * t) * decay3 * 0.08
            val combined = (s1 + s2 + s3).coerceIn(-1.0, 1.0)
            buffer[i] = (combined * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }
}
