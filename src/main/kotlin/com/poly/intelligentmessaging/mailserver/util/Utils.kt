package com.poly.intelligentmessaging.mailserver.util

import kotlin.random.Random

fun generatePassword(): String {
    val alphabet = ALPHABET
    val password = StringBuilder()
    for (i in 0 until 12) {
        val isUpper = Random.nextInt(2)
        val symbol = alphabet[Random.nextInt(0, alphabet.length)]
        password.append(if (isUpper == 0 && symbol.isLetter()) symbol.lowercase() else symbol)
    }
    return password.toString()
}