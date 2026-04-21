package com.shubham.hangman

import com.shubham.hangman.model.GameEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
    @Test
    fun displayWordRevealsRepeatedLetters() {
        val word = "KANGAROO"
        val visible = GameEngine.displayWord(word, setOf('A', 'O'))

        assertEquals("_ A _ _ A _ O O", visible)
    }

    @Test
    fun scoreForWinRewardsCleanerRounds() {
        val noHintScore = GameEngine.scoreForWin(
            wrongGuessCount = 1,
            hintUsed = false,
            currentStreak = 3,
        )
        val hintScore = GameEngine.scoreForWin(
            wrongGuessCount = 1,
            hintUsed = true,
            currentStreak = 0,
        )

        assertTrue(noHintScore.total > hintScore.total)
    }

    @Test
    fun scoreForWinAddsVisibleBreakdown() {
        val reward = GameEngine.scoreForWin(
            wrongGuessCount = 0,
            hintUsed = false,
            currentStreak = 5,
        )

        assertTrue(reward.breakdown.contains("Perfect"))
        assertTrue(reward.breakdown.contains("Streak"))
    }
}
