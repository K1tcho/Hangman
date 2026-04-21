package com.shubham.hangman.model

/** Per-language UI strings */
object AppStrings {
    fun chancesLeft(n: Int, lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "$n chances left"
        AppLanguage.Polish -> "Pozostało prób: $n"
        AppLanguage.Russian -> "Попыток осталось: $n"
    }

    fun clueButton(lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Clue"
        AppLanguage.Polish -> "Podpowiedź"
        AppLanguage.Russian -> "Подсказка"
    }

    fun hintButton(lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Hint"
        AppLanguage.Polish -> "Litera"
        AppLanguage.Russian -> "Буква"
    }

    fun skipButton(lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Skip"
        AppLanguage.Polish -> "Pomiń"
        AppLanguage.Russian -> "Пропуск"
    }

    fun nextButton(lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Next"
        AppLanguage.Polish -> "Dalej"
        AppLanguage.Russian -> "Далее"
    }

    fun guessCorrect(letter: Char, lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Nice! \"$letter\" is in the word."
        AppLanguage.Polish -> "Dobrze! \"$letter\" jest w słowie."
        AppLanguage.Russian -> "Верно! «$letter» есть в слове."
    }

    fun guessWrong(letter: Char, remaining: Int, lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "No \"$letter\". $remaining tries left."
        AppLanguage.Polish -> "Nie ma \"$letter\". Zostało prób: $remaining."
        AppLanguage.Russian -> "Нет «$letter». Попыток: $remaining."
    }

    fun roundLost(answer: String, lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Round over. The word was $answer."
        AppLanguage.Polish -> "Koniec rundy. Słowo to: $answer."
        AppLanguage.Russian -> "Раунд окончен. Слово: $answer."
    }

    fun roundWon(points: Int, lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Solved! +$points points."
        AppLanguage.Polish -> "Rozwiązano! +$points punktów."
        AppLanguage.Russian -> "Разгадано! +$points очков."
    }

    fun hintUsed(letter: Char, lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Hint: \"$letter\" revealed."
        AppLanguage.Polish -> "Podpowiedź: ujawniono \"$letter\"."
        AppLanguage.Russian -> "Подсказка: «$letter» раскрыта."
    }

    fun clueRevealed(lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Clue revealed. Keep rolling!"
        AppLanguage.Polish -> "Wskazówka ujawniona!"
        AppLanguage.Russian -> "Подсказка раскрыта!"
    }

    fun freshRound(category: String, lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Fresh round. $category is ready."
        AppLanguage.Polish -> "Nowa runda. $category gotowe."
        AppLanguage.Russian -> "Новый раунд. $category готово."
    }

    fun categoryLoaded(category: String, lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "$category loaded."
        AppLanguage.Polish -> "Kategoria: $category."
        AppLanguage.Russian -> "Категория: $category."
    }

    fun languageChanged(lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Language set to English."
        AppLanguage.Polish -> "Język: Polski."
        AppLanguage.Russian -> "Язык: Русский."
    }

    fun streakSaved(score: Int, streak: Int, lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Run saved: $score pts, $streak streak."
        AppLanguage.Polish -> "Zapis: $score pkt, seria $streak."
        AppLanguage.Russian -> "Сохранено: $score очков, серия $streak."
    }

    fun lostStreakReset(lang: AppLanguage): String = when (lang) {
        AppLanguage.English -> "Score saved, streak reset."
        AppLanguage.Polish -> "Wynik zapisany, seria zresetowana."
        AppLanguage.Russian -> "Счёт сохранён, серия сброшена."
    }
}
