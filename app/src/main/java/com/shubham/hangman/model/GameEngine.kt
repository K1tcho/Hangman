package com.shubham.hangman.model

object GameEngine {
    fun normalizeAnswer(answer: String): String = answer.uppercase()

    fun displayWord(answer: String, guessedLetters: Set<Char>): String =
        answer.map { char ->
            when {
                char == ' ' -> "  "
                !char.isLetter() -> char.toString()
                guessedLetters.contains(char) -> char.toString()
                else -> "_"
            }
        }.joinToString(" ")

    fun isSolved(answer: String, guessedLetters: Set<Char>): Boolean =
        answer.filter(Char::isLetter).all(guessedLetters::contains)

    fun hiddenLetters(answer: String, guessedLetters: Set<Char>): List<Char> =
        answer
            .asSequence()
            .filter(Char::isLetter)
            .filterNot(guessedLetters::contains)
            .distinct()
            .toList()

    fun scoreForWin(
        wrongGuessCount: Int,
        hintUsed: Boolean,
        currentStreak: Int,
    ): RoundReward {
        val baseScore = 28
        val accuracyBonus = (MAX_WRONG_GUESSES - wrongGuessCount) * 6
        val cleanRoundBonus = if (hintUsed) 0 else 12
        val perfectRoundBonus = if (wrongGuessCount == 0) 18 else 0
        val streakBonus = when {
            currentStreak >= 8 -> 24
            currentStreak >= 5 -> 16
            currentStreak >= 3 -> 10
            else -> 0
        }

        val total = baseScore + accuracyBonus + cleanRoundBonus + perfectRoundBonus + streakBonus
        val breakdown = buildList {
            add("Base +$baseScore")
            add("Accuracy +$accuracyBonus")
            if (cleanRoundBonus > 0) add("Clean +$cleanRoundBonus")
            if (perfectRoundBonus > 0) add("Perfect +$perfectRoundBonus")
            if (streakBonus > 0) add("Streak +$streakBonus")
        }.joinToString("  |  ")

        return RoundReward(total = total, breakdown = breakdown)
    }

    /** Returns the correct keyboard rows for the given language */
    fun keyboardRows(language: AppLanguage): List<String> = when (language) {
        AppLanguage.English -> listOf("ABCDEFG", "HIJKLMN", "OPQRST", "UVWXYZ")
        AppLanguage.Polish  -> listOf("ABCDEFG", "HIJKLMN", "OPQRST", "UVWXYZ", "ĄĆĘŁŃÓŚŹŻ")
        AppLanguage.Russian -> listOf("АБВГДЕЁЖ", "ЗИЙКЛМНО", "ПРСТУФХЦ", "ЧШЩЪЫЬЭЮЯ")
    }
}
