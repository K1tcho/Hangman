package com.shubham.hangman

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shubham.hangman.data.GamePreferences
import com.shubham.hangman.data.SavedProgress
import com.shubham.hangman.data.WordRepository
import com.shubham.hangman.model.AppLanguage
import com.shubham.hangman.model.AppStrings
import com.shubham.hangman.model.GameEngine
import com.shubham.hangman.model.HangmanCategory
import com.shubham.hangman.model.HangmanUiState
import com.shubham.hangman.model.MAX_WRONG_GUESSES
import com.shubham.hangman.model.RoundStatus
import com.shubham.hangman.model.SoundEffect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HangmanViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = GamePreferences(application)
    private val savedProgress = preferences.loadProgress()
    private val recentWordsByCategory = mutableMapOf<HangmanCategory, ArrayDeque<String>>()

    private val _uiState = MutableStateFlow(
        HangmanUiState(
            selectedCategory = savedProgress.category,
            selectedLanguage = savedProgress.language,
            score = savedProgress.score,
            streak = savedProgress.streak,
            highScore = savedProgress.highScore,
            bestStreak = savedProgress.bestStreak,
        ),
    )
    val uiState: StateFlow<HangmanUiState> = _uiState.asStateFlow()

    private val _soundEffects = MutableSharedFlow<SoundEffect>(extraBufferCapacity = 8)
    val soundEffects: SharedFlow<SoundEffect> = _soundEffects.asSharedFlow()

    init {
        startNewRound(announce = false)
    }

    fun selectCategory(category: HangmanCategory) {
        rememberCurrentWord()
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                bannerMessage = AppStrings.categoryLoaded(category.title(current.selectedLanguage), current.selectedLanguage),
                rewardBreakdown = "",
            )
        }
        startNewRound(announce = true)
    }

    fun selectLanguage(language: AppLanguage) {
        val current = _uiState.value
        if (current.selectedLanguage == language) return

        _uiState.update {
            it.copy(
                selectedLanguage = language,
                bannerMessage = AppStrings.languageChanged(language),
            )
        }
        persistStats()
        startNewRound(announce = true)
    }

    fun guessLetter(letter: Char) {
        val guess = letter.uppercaseChar()
        val state = _uiState.value
        if (!guess.isLetter() || !state.isRoundActive || guess in state.guessedLetters || guess in state.wrongLetters) {
            return
        }

        if (state.answer.contains(guess)) {
            val updatedGuesses = state.guessedLetters + guess
            if (GameEngine.isSolved(state.answer, updatedGuesses)) {
                finishRound(updatedGuesses = updatedGuesses)
            } else {
                _uiState.update {
                    it.copy(
                        guessedLetters = updatedGuesses,
                        bannerMessage = AppStrings.guessCorrect(guess, it.selectedLanguage),
                        rewardBreakdown = "",
                    )
                }
                emitSound(SoundEffect.Correct)
            }
            return
        }

        val updatedWrong = state.wrongLetters + guess
        if (updatedWrong.size >= MAX_WRONG_GUESSES) {
            _uiState.update {
                it.copy(
                    wrongLetters = updatedWrong,
                    status = RoundStatus.Lost,
                    streak = 0,
                    bannerMessage = AppStrings.roundLost(it.answer, it.selectedLanguage),
                    rewardBreakdown = AppStrings.lostStreakReset(it.selectedLanguage),
                )
            }
            persistStats()
            emitSound(SoundEffect.Lose)
        } else {
            _uiState.update {
                it.copy(
                    wrongLetters = updatedWrong,
                    bannerMessage = AppStrings.guessWrong(guess, MAX_WRONG_GUESSES - updatedWrong.size, it.selectedLanguage),
                    rewardBreakdown = "",
                )
            }
            emitSound(SoundEffect.Wrong)
        }
    }

    fun useHint() {
        val state = _uiState.value
        if (!state.isRoundActive || state.hintUsed) {
            return
        }

        val hidden = GameEngine.hiddenLetters(state.answer, state.guessedLetters)
        if (hidden.isEmpty()) return

        val revealedLetter = hidden.random()
        val updatedGuesses = state.guessedLetters + revealedLetter

        _uiState.update {
            it.copy(
                guessedLetters = updatedGuesses,
                hintUsed = true,
                clueRevealed = true,
                bannerMessage = AppStrings.hintUsed(revealedLetter, it.selectedLanguage),
                rewardBreakdown = "",
            )
        }
        persistStats()
        emitSound(SoundEffect.Hint)

        if (GameEngine.isSolved(state.answer, updatedGuesses)) {
            finishRound(updatedGuesses = updatedGuesses)
        }
    }

    fun revealClue() {
        _uiState.update { current ->
            if (!current.clueRevealed && current.isRoundActive) {
                current.copy(
                    clueRevealed = true,
                    bannerMessage = AppStrings.clueRevealed(current.selectedLanguage),
                    rewardBreakdown = "",
                )
            } else {
                current
            }
        }
    }

    fun startNewRound(announce: Boolean = true) {
        val current = _uiState.value
        rememberCurrentWord()
        val puzzle = WordRepository.randomWord(
            language = current.selectedLanguage,
            category = current.selectedCategory,
            previousAnswer = current.answer.ifBlank { null },
            recentAnswers = recentWordsByCategory[current.selectedCategory].orEmpty(),
        )
        _uiState.update {
            it.copy(
                answer = puzzle.answer,
                clue = puzzle.clue,
                guessedLetters = emptySet(),
                wrongLetters = emptySet(),
                hintUsed = false,
                clueRevealed = false,
                status = RoundStatus.Playing,
                bannerMessage = AppStrings.freshRound(current.selectedCategory.title(current.selectedLanguage), current.selectedLanguage),
                rewardBreakdown = if (current.streak > 0) {
                    AppStrings.streakSaved(current.score, current.streak, current.selectedLanguage)
                } else {
                    ""
                },
            )
        }
        persistStats()
        if (announce) emitSound(SoundEffect.NewRound)
    }

    private fun rememberCurrentWord() {
        val state = _uiState.value
        val answer = state.answer.ifBlank { return }
        val category = state.selectedCategory
        val history = recentWordsByCategory.getOrPut(category) { ArrayDeque() }
        history.remove(answer)
        history.addFirst(answer)

        val historyLimit = 10
        while (history.size > historyLimit) {
            history.removeLast()
        }
    }

    private fun finishRound(updatedGuesses: Set<Char>) {
        val current = _uiState.value
        val reward = GameEngine.scoreForWin(
            wrongGuessCount = current.wrongGuessCount,
            hintUsed = current.hintUsed,
            currentStreak = current.streak,
        )
        val updatedScore = current.score + reward.total
        val updatedStreak = current.streak + 1
        val updatedHighScore = maxOf(current.highScore, updatedScore)
        val updatedBestStreak = maxOf(current.bestStreak, updatedStreak)

        _uiState.update {
            it.copy(
                guessedLetters = updatedGuesses,
                status = RoundStatus.Won,
                score = updatedScore,
                streak = updatedStreak,
                highScore = updatedHighScore,
                bestStreak = updatedBestStreak,
                bannerMessage = AppStrings.roundWon(reward.total, it.selectedLanguage),
                rewardBreakdown = reward.breakdown,
            )
        }
        persistStats()
        emitSound(SoundEffect.Win)
    }

    private fun persistStats() {
        val state = _uiState.value
        preferences.saveProgress(
            SavedProgress(
                score = state.score,
                streak = state.streak,
                highScore = state.highScore,
                bestStreak = state.bestStreak,
                category = state.selectedCategory,
                language = state.selectedLanguage,
            ),
        )
    }

    private fun emitSound(effect: SoundEffect) {
        viewModelScope.launch {
            _soundEffects.emit(effect)
        }
    }
}
