package com.shubham.hangman.model

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val flagEmoji: String,
) {
    English("EN", "English", "🇬🇧"),
    Polish("PL", "Polski", "🇵🇱"),
    Russian("RU", "Русский", "🇷🇺"),
}
