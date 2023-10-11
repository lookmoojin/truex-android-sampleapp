package com.truedigital.features.truecloudv3.util

object TrueCloudV3GroupAlphabetUtils {

    private const val ENGLISH_REGEX = "[a-zA-Z]"
    private const val THAI_REGEX = "[\u0E00-\u0E7F]+"
    const val DEFAULT_ALPHABET = "#"

    fun getFirstAlphabet(value: String): String {
        val alphabet = checkCharSequenceIsEmpty(value)
        return if (
            alphabet.matches(Regex(ENGLISH_REGEX)) ||
            alphabet.matches(Regex(THAI_REGEX))
        ) {
            alphabet
        } else {
            DEFAULT_ALPHABET
        }
    }

    private fun checkCharSequenceIsEmpty(value: String): String {
        var alphabet = DEFAULT_ALPHABET
        value.runCatching {
            alphabet = value.first().uppercase()
        }
        return alphabet
    }
}
