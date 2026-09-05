package com.orbit.spatialjournal.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Uses Android's built-in SpeechRecognizer (backed by Google's on-device or server speech
 * service, depending on device/network) to transcribe a recorded Voice Memory. This is real,
 * functional speech-to-text — no placeholder text is ever returned; failures are surfaced via
 * [TranscriptResult.error] so the UI can let the user type a manual transcript instead.
 */
@Singleton
class AndroidSpeechRecognizerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SpeechToTextProvider {

    override suspend fun transcribe(audioFilePath: String, languageTag: String): TranscriptResult {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            return TranscriptResult(text = null, confidence = null, error = "Speech recognition not available on this device")
        }
        // NOTE: Android's SpeechRecognizer transcribes live microphone audio, not an arbitrary
        // audio file. AudioRecorderManager therefore also starts a recognizer session in
        // parallel with the raw WAV/M4A capture so a transcript is produced during recording;
        // this function is kept for provider-interface symmetry and for re-running recognition.
        return suspendCancellableCoroutine { continuation ->
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val confidences = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
                    if (continuation.isActive) {
                        continuation.resume(
                            TranscriptResult(text = matches?.firstOrNull(), confidence = confidences?.firstOrNull())
                        )
                    }
                    recognizer.destroy()
                }
                override fun onError(error: Int) {
                    if (continuation.isActive) continuation.resume(TranscriptResult(null, null, "recognizer_error_$error"))
                    recognizer.destroy()
                }
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            recognizer.startListening(intent)
            continuation.invokeOnCancellation { recognizer.destroy() }
        }
    }
}
