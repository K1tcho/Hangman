package com.shubham.hangman.model

data class HangmanUiState(
    val selectedCategory: HangmanCategory = HangmanCategory.Animals,
    val selectedLanguage: AppLanguage = AppLanguage.English,
    val answer: String = "",
    val clue: String = "",
    val guessedLetters: Set<Char> = emptySet(),
    val wrongLetters: Set<Char> = emptySet(),
    val score: Int = 0,
    val streak: Int = 0,
    val highScore: Int = 0,
    val bestStreak: Int = 0,
    val hintUsed: Boolean = false,
    val clueRevealed: Boolean = false,
    val status: RoundStatus = RoundStatus.Playing,
    val bannerMessage: String = "",
    val rewardBreakdown: String = "",
) {
    val wrongGuessCount: Int = wrongLetters.size
    val remainingAttempts: Int = MAX_WRONG_GUESSES - wrongLetters.size
    val visibleWord: String = GameEngine.displayWord(answer, guessedLetters)
    val isRoundActive: Boolean = status == RoundStatus.Playing
}

const val MAX_WRONG_GUESSES = 6
