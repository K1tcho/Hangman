package com.shubham.hangman.model

enum class HangmanCategory {
    Animals, Movies, Countries, Science, Food;

    fun title(lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> when (this) {
            Animals -> "Animals"; Movies -> "Movies"; Countries -> "Countries"
            Science -> "Science"; Food -> "Food"
        }
        AppLanguage.Polish -> when (this) {
            Animals -> "Zwierzęta"; Movies -> "Filmy"; Countries -> "Kraje"
            Science -> "Nauka"; Food -> "Jedzenie"
        }
        AppLanguage.Russian -> when (this) {
            Animals -> "Животные"; Movies -> "Фильмы"; Countries -> "Страны"
            Science -> "Наука"; Food -> "Еда"
        }
    }

    fun subtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> when (this) {
            Animals -> "Wild names and familiar creatures"
            Movies -> "Iconic titles and cinema favorites"
            Countries -> "Places from around the world"
            Science -> "Smart words from labs and space"
            Food -> "Savory, sweet, and snackable picks"
        }
        AppLanguage.Polish -> when (this) {
            Animals -> "Dzikie i domowe stworzenia"
            Movies -> "Kultowe tytuły filmowe"
            Countries -> "Miejsca z całego świata"
            Science -> "Słowa z laboratoriów i kosmosu"
            Food -> "Słodkie, słone i smaczne"
        }
        AppLanguage.Russian -> when (this) {
            Animals -> "Дикие и домашние существа"
            Movies -> "Культовые кинотитулы"
            Countries -> "Места со всего мира"
            Science -> "Слова из лабораторий и космоса"
            Food -> "Сладкое, солёное и вкусное"
        }
    }
}
