package com.example.employeecontact.application.service

import com.example.employeecontact.domain.exception.InvalidDataFormatException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateParser {
    private val formatters = listOf(
        DateTimeFormatter.ofPattern("yyyy.MM.dd"),  // CSV format: 2018.03.07
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),  // JSON format: 2012-01-05
        DateTimeFormatter.ofPattern("yyyy/MM/dd")   // Alternative format
    )

    fun parse(dateStr: String): LocalDate {
        val trimmed = dateStr.trim()
        for (formatter in formatters) {
            try {
                return LocalDate.parse(trimmed, formatter)
            } catch (e: DateTimeParseException) {
                continue
            }
        }
        throw InvalidDataFormatException.invalidDate(trimmed)
    }
}
