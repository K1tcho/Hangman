package com.shubham.hangman

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shubham.hangman.ui.HangmanApp
import com.shubham.hangman.ui.theme.HangmanTheme
import com.shubham.hangman.model.SoundEffect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<HangmanViewModel>()
    private val toneGenerator by lazy { ToneGenerator(AudioManager.STREAM_MUSIC, 70) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.soundEffects.collect(::playToneFor)
            }
        }

        setContent {
            HangmanTheme {
                HangmanApp(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        toneGenerator.release()
        super.onDestroy()
    }

    private fun playToneFor(effect: SoundEffect) {
        when (effect) {
            SoundEffect.Correct -> toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            SoundEffect.Wrong -> toneGenerator.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 180)
            SoundEffect.Win -> toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 280)
            SoundEffect.Lose -> toneGenerator.startTone(ToneGenerator.TONE_SUP_ERROR, 260)
            SoundEffect.Hint -> toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 140)
            SoundEffect.NewRound -> toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 110)
        }
    }
}
