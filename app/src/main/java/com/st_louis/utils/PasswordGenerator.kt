package com.st_louis.utils

import java.util.Locale
import java.security.SecureRandom

class PasswordGenerator {
    companion object {
        private val EASY_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        private val SECURE_RANDOM = SecureRandom()

        // For Pre-Primary - Very simple, easy to remember
        fun generatePrePrimaryPassword(name: String, length: Int = 8): String {
            val namePart = name.trim().split(" ").take(2).joinToString("") { word ->
                word.lowercase().take(3)
            }

            val randomPart = (10..99).random()
            val specialChar = listOf('@', '#', '$')[SECURE_RANDOM.nextInt(3)]

            return "${namePart.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${randomPart}${specialChar}"
        }

        // For Primary - Slightly more complex
        fun generatePrimaryPassword(name: String, length: Int = 10): String {
            val namePart = name.trim().split(" ").take(2).joinToString("") { word ->
                word.lowercase().take(2)
            }

            val randomPart = (100..999).random()
            val specialChar = listOf('@', '#', '$', '&', '!')[SECURE_RANDOM.nextInt(5)]
            val number = SECURE_RANDOM.nextInt(10)

            return "${namePart.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${randomPart}${specialChar}${number}"
        }

        // General temporary password
        fun generateTemporaryPassword(length: Int = 12): String {
            return (1..length)
                .map { EASY_CHARACTERS[SECURE_RANDOM.nextInt(EASY_CHARACTERS.length)] }
                .joinToString("")
        }

        // Generate parent/guardian portal password
        fun generateParentPassword(studentName: String): String {
            val studentPart = studentName.trim().split(" ").take(2).joinToString("") { word ->
                word.lowercase().take(2)
            }
            val random = (1000..9999).random()
            return "parent${studentPart}${random}"
        }
    }
}