package com.orbit.spatialjournal.voice

/** Provider interface so the transcription engine can be swapped (on-device model, cloud, etc.). */
interface SpeechToTextProvider {
    suspend fun transcribe(audioFilePath: String, languageTag: String): TranscriptResult
}

data class TranscriptResult(val text: String?, val confidence: Float?, val error: String? = null)
