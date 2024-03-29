package com.example.android.mines

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log

/**
 * Class used to interface with the [TextToSpeech] class.
 */
class Narrator(context: Context) : OnInitListener {
    /**
     * [TextToSpeech] instance we will use to synthesize speech
     */
    private var mTts: TextToSpeech? = TextToSpeech(context, this)

    /**
     * Called to signal the completion of the TextToSpeech engine initialization. If our [Int]
     * parameter [status] is [TextToSpeech.SUCCESS] we log the fact that: "TextToSpeech successfully
     * initialized", initialize our [Bundle] variable `val dummyBundle` to `null` (a kludge that the
     * compiler needed when I wrote this, might be fixed?) then call the [TextToSpeech.speak] method
     * of [mTts] with the text to be spoken: "Welcome to Mine Sweeper", with the `queueMode`
     * [TextToSpeech.QUEUE_ADD] (the new entry is added at the end of the playback queue), with
     * `dummyBundle` as the parameters for the request (aka `null`), and `null` as the `utteranceId`
     * (unique identifier for this request).
     *
     * If [status] is NOT [TextToSpeech.SUCCESS] we log the message "Could not initialize TTS."
     *
     * @param status [TextToSpeech.SUCCESS] or [TextToSpeech.ERROR].
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.i(TAG, "TextToSpeech successfully initialized")
            val dummyBundle: Bundle? = null
            mTts?.speak(
                "Welcome to Mine Sweeper",
                TextToSpeech.QUEUE_ADD,
                dummyBundle,
                null
            )
        } else {
            Log.e(TAG, "Could not initialize TTS.")
        }
    }

    /**
     * Called to interrupt the current utterance (whether played or rendered to file), discard other
     * utterances in the queue, release the resources used by the [TextToSpeech] engine with a call
     * to [TextToSpeech.shutdown], and set our [TextToSpeech] field [mTts] to `null` so that it can
     * be garbage collected.
     */
    fun shutDown() {
        Log.i(TAG, "Shutting down")
        mTts?.stop()
        mTts?.shutdown()
        mTts = null
    }

    /**
     * Calls the [TextToSpeech.speak] method of our field [mTts] to have it speak the [String]
     * parameter [text]. We initialize our [Bundle] variable `val dummyBundle` to `null` (a kludge
     * that the compiler needed when I wrote this, might be fixed?), and initialize our [Int] variable
     * `val queMode` to [TextToSpeech.QUEUE_FLUSH] if our [flush] parameter is `true` or to
     * [TextToSpeech.QUEUE_ADD] if it is `false`. We then call the [TextToSpeech.speak] method of
     * [mTts] with our parameter [text] as the text to be spoken, with the `queueMode` either
     * [TextToSpeech.QUEUE_FLUSH] (all entries in the playback queue are dropped and replaced by the
     * new entry) or [TextToSpeech.QUEUE_ADD] (the new entry is added at the end of the playback queue)
     * depending on whether our [Boolean] parameter [flush] is `true` or `false`, with `dummyBundle`
     * as the parameters for the request (aka `null`), and `null` as the `utteranceId` (unique
     * identifier for this request).
     *
     * @param text the text to be spoken, must be no longer than [TextToSpeech.getMaxSpeechInputLength]
     * @param flush if `true` our call to the [TextToSpeech.speak] method of [mTts] will use the quemode
     * [TextToSpeech.QUEUE_FLUSH] (all entries in the playback queue are dropped and replaced by the
     * new entry) and if `false` will use the quemode  [TextToSpeech.QUEUE_ADD] which will add the
     * [text] to the end of its queue.
     */
    fun tellUser(text: String, flush: Boolean = false) {
        val dummyBundle: Bundle? = null
        val queMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        mTts?.speak(
            text,
            queMode,
            dummyBundle,
            null
        )
    }

    companion object {
        /**
         * TAG used for logging.
         */
        const val TAG: String = "Narrator"
    }
}