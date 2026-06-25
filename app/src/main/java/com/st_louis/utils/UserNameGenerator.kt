package com.st_louis.utils

class UserNameGenerator {
    companion object {
        fun generateForSchool(
            name: String,
            className: String = "",
            admissionNumber: String = "",
            level: String = "PRIMARY"
        ): String {
            // Clean name - take first 4 letters
            val cleanName = name.trim().split(" ").joinToString("") { word ->
                word.lowercase().take(2)
            }.take(6)

            // Level prefix
            val levelPrefix = when (level) {
                "PRE_PRIMARY" -> "pp"
                "PRIMARY" -> "p"
                else -> "s"
            }

            // Class abbreviation
            val classAbbr = when {
                className.contains("Baby") -> "baby"
                className.contains("PP1") -> "pp1"
                className.contains("PP2") -> "pp2"
                className.contains("Grade") -> "g${className.replace("Grade", "").trim()}"
                className.matches(Regex("^\\d$")) -> "g$className"
                else -> "cls"
            }

            // Take last 4 digits of admission number if available
            val admissionSuffix = if (admissionNumber.isNotEmpty() && admissionNumber.length >= 4) {
                admissionNumber.takeLast(4)
            } else {
                String.format("%04d", (1000..9999).random())
            }

            return "${levelPrefix}${classAbbr}${cleanName}${admissionSuffix}".lowercase().take(15)
        }

        fun generateStudentNumber(className: String, admissionNumber: String): String {
            val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val classCode = when {
                className.contains("Baby") -> "BC"
                className.contains("PP1") -> "PP1"
                className.contains("PP2") -> "PP2"
                className.contains("Grade") -> "G${className.replace("Grade", "").trim()}"
                else -> "STU"
            }
            return "${classCode}${year.toString().takeLast(2)}${admissionNumber.takeLast(4)}"
        }
    }
}