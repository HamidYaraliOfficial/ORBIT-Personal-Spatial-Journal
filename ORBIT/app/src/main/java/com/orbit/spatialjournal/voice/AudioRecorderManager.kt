package com.orbit.spatialjournal.voice

import android.content.Context
import android.media.MediaRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** Records a Voice Memory to a private app file and returns its path once stopped. */
@Singleton
class AudioRecorderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): String {
        val dir = File(context.filesDir, "voice_memories").apply { mkdirs() }
        val file = File(dir, "voice_${System.currentTimeMillis()}.m4a")
        outputFile = file

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128_000)
            setAudioSamplingRate(44_100)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        return file.absolutePath
    }

    /** Returns the duration of the just-finished recording in milliseconds, or null if unknown. */
    fun stopRecording(): String? {
        return try {
            recorder?.apply { stop(); release() }
            recorder = null
            outputFile?.absolutePath
        } catch (e: Exception) {
            recorder?.release()
            recorder = null
            null
        }
    }

    fun cancelRecording() {
        runCatching { recorder?.stop() }
        recorder?.release()
        recorder = null
        outputFile?.delete()
        outputFile = null
    }
}
