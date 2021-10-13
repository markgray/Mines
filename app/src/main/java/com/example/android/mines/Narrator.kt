package com.example.android.mines

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log

class Narrator(context: Context): OnInitListener {
    /**
     * [TextToSpeech] instance we will use to synthesize speech
     */
    private var mTts: TextToSpeech? = TextToSpeech(context, this)

    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
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

    fun shutDown() {
        Log.i(TAG, "Shutting down")
        mTts?.stop()
        mTts?.shutdown()
        mTts = null
    }

    fun tellUser(text: String) {
        val dummyBundle: Bundle? = null
        mTts?.speak(
            text,
            TextToSpeech.QUEUE_ADD,
            dummyBundle,
            null
        )
    }

    companion object {
        const val TAG = "Narrator"
    }
}