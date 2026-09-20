package com.example.game.core

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object AudioManager {
  var isSoundEnabled: Boolean = true
  var isMusicEnabled: Boolean = true

  private val sampleRate = 22050
  private val scope = CoroutineScope(Dispatchers.Default)
  private var ambientMusicJob: Job? = null

  // Pentatonic Kalimba frequencies: C5, D5, E5, G5, A5, C6
  private val kalimbaNotes = listOf(523.25f, 587.33f, 659.25f, 783.99f, 880.00f, 1046.50f)

  fun playTileClick() {
    if (!isSoundEnabled) return
    scope.launch {
      // Crisp wooden clack sound (fast decay, slight frequency drop)
      playTone(freq = 640f, durationMs = 45, type = ToneType.WOOD_CLACK)
    }
  }

  fun playTileMatch(comboStreak: Int = 1) {
    if (!isSoundEnabled) return
    scope.launch {
      // Pick note from pentatonic scale based on combo
      val noteIndex = ((comboStreak - 1).coerceAtLeast(0)) % kalimbaNotes.size
      val freq = kalimbaNotes[noteIndex]
      playTone(freq = freq, durationMs = 280, type = ToneType.KALIMBA)
    }
  }

  fun playErrorThud() {
    if (!isSoundEnabled) return
    scope.launch {
      playTone(freq = 140f, durationMs = 80, type = ToneType.MUTED_THUD)
    }
  }

  fun playPowerUp() {
    if (!isSoundEnabled) return
    scope.launch {
      // Rising sparkle frequencies
      playTone(freq = 440f, durationMs = 120, type = ToneType.CHIME)
      delay(60)
      playTone(freq = 660f, durationMs = 120, type = ToneType.CHIME)
      delay(60)
      playTone(freq = 880f, durationMs = 180, type = ToneType.CHIME)
    }
  }

  fun playShuffle() {
    if (!isSoundEnabled) return
    scope.launch {
      // Shaker rattle
      for (i in 0 until 4) {
        playTone(freq = 1200f + (i * 200), durationMs = 40, type = ToneType.SHAKER)
        delay(35)
      }
    }
  }

  fun playVictoryFanfare() {
    if (!isSoundEnabled) return
    scope.launch {
      val fanfare = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
      fanfare.forEachIndexed { idx, freq ->
        playTone(freq = freq, durationMs = if (idx == fanfare.size - 1) 500 else 180, type = ToneType.KALIMBA)
        delay(120)
      }
    }
  }

  fun startAmbientMusic() {
    if (!isMusicEnabled || ambientMusicJob?.isActive == true) return
    ambientMusicJob = scope.launch {
      val gentleMelody = listOf(
        Pair(523.25f, 300L),
        Pair(659.25f, 300L),
        Pair(783.99f, 400L),
        Pair(659.25f, 300L),
        Pair(880.00f, 500L),
        Pair(783.99f, 300L),
        Pair(523.25f, 600L)
      )
      var noteIdx = 0
      while (isActive && isMusicEnabled) {
        val (freq, dur) = gentleMelody[noteIdx % gentleMelody.size]
        playTone(freq = freq * 0.75f, durationMs = 350, type = ToneType.KALIMBA, volume = 0.25f)
        delay(dur + 350L)
        noteIdx++
      }
    }
  }

  fun stopAmbientMusic() {
    ambientMusicJob?.cancel()
    ambientMusicJob = null
  }

  private enum class ToneType { WOOD_CLACK, KALIMBA, MUTED_THUD, CHIME, SHAKER }

  private fun playTone(freq: Float, durationMs: Int, type: ToneType, volume: Float = 0.6f) {
    try {
      val numSamples = (sampleRate * durationMs) / 1000
      val buffer = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val t = i.toFloat() / sampleRate
        val progress = i.toFloat() / numSamples

        // Envelope
        val env = when (type) {
          ToneType.WOOD_CLACK -> (1.0f - progress) * (1.0f - progress) * (1.0f - progress)
          ToneType.KALIMBA -> (1.0f - progress * 0.85f) * kotlin.math.exp(-progress * 3.5f)
          ToneType.MUTED_THUD -> (1.0f - progress)
          ToneType.CHIME -> (1.0f - progress * 0.7f)
          ToneType.SHAKER -> (kotlin.random.Random.nextFloat() * 2f - 1f) * (1.0f - progress)
        }

        val rawSample = when (type) {
          ToneType.WOOD_CLACK -> {
            sin(2.0 * Math.PI * freq * (1.0 - progress * 0.3) * t).toFloat()
          }
          ToneType.KALIMBA -> {
            // Fundamental + overtone for metallic tines resonance
            (sin(2.0 * Math.PI * freq * t) + 0.35 * sin(2.0 * Math.PI * freq * 2.76 * t)).toFloat()
          }
          ToneType.MUTED_THUD -> {
            sin(2.0 * Math.PI * freq * t).toFloat()
          }
          ToneType.CHIME -> {
            (sin(2.0 * Math.PI * freq * t) + 0.25 * sin(2.0 * Math.PI * freq * 2.0 * t)).toFloat()
          }
          ToneType.SHAKER -> {
            (kotlin.random.Random.nextFloat() * 2.0f - 1.0f)
          }
        }

        val sample = (rawSample * env * volume * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
        buffer[i] = sample.toShort()
      }

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
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

      track.write(buffer, 0, buffer.size)
      track.play()
      // Release after playback finished
      scope.launch {
        delay(durationMs + 100L)
        try {
          track.stop()
          track.release()
        } catch (_: Exception) {}
      }
    } catch (_: Exception) {
      // Audio fallback graceful
    }
  }
}
