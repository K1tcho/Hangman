package com.shubham.hangman.data

import android.content.Context
import com.shubham.hangman.model.AppLanguage
import com.shubham.hangman.model.HangmanCategory

data class SavedProgress(
    val score: Int,
    val streak: Int,
    val highScore: Int,
    val bestStreak: Int,
    val category: HangmanCategory,
    val language: AppLanguage,
)

class GamePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("hangman_prefs", Context.MODE_PRIVATE)

    fun loadProgress(): SavedProgress {
        val category = HangmanCategory.entries.firstOrNull { it.name == prefs.getString(KEY_CATEGORY, null) }
            ?: HangmanCategory.Animals
        val language = AppLanguage.entries.firstOrNull { it.name == prefs.getString(KEY_LANGUAGE, null) }
            ?: AppLanguage.English

        return SavedProgress(
            score = prefs.getInt(KEY_SCORE, 0),
            streak = prefs.getInt(KEY_STREAK, 0),
            highScore = prefs.getInt(KEY_HIGH_SCORE, 0),
            bestStreak = prefs.getInt(KEY_BEST_STREAK, 0),
            category = category,
            language = language,
        )
    }

    fun saveProgress(progress: SavedProgress) {
        prefs.edit()
            .putInt(KEY_SCORE, progress.score)
            .putInt(KEY_STREAK, progress.streak)
            .putInt(KEY_HIGH_SCORE, progress.highScore)
            .putInt(KEY_BEST_STREAK, progress.bestStreak)
            .putString(KEY_CATEGORY, progress.category.name)
            .putString(KEY_LANGUAGE, progress.language.name)
            .apply()
    }

    private companion object {
        const val KEY_SCORE = "score"
        const val KEY_STREAK = "streak"
        const val KEY_HIGH_SCORE = "high_score"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_CATEGORY = "last_category"
        const val KEY_LANGUAGE = "last_language"
    }
}
