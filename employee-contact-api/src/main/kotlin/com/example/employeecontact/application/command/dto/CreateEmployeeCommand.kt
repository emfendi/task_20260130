package com.example.employeecontact.application.command.dto

import com.example.employeecontact.domain.exception.InvalidDataFormatException
import java.time.LocalDate

data class CreateEmployeeCommand(
    val name: String,
    val email: String,
    val tel: String,
    val joined: LocalDate
) {
    init {
        validateName(name)
        validateEmail(email)
        validateTel(tel)
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private val TEL_REGEX = Regex("^\\d{2,4}-?\\d{3,4}-?\\d{4}$")

        private fun validateName(name: String) {
            val trimmed = name.trim()
            if (trimmed.isBlank()) {
                throw InvalidDataFormatException.invalidField("name", "Name cannot be blank")
            }
            if (trimmed.length > 100) {
                throw InvalidDataFormatException.invalidField("name", "Name too long (max 100 characters)")
            }
        }

        private fun validateEmail(email: String) {
            val trimmed = email.trim()
            if (!EMAIL_REGEX.matches(trimmed)) {
                throw InvalidDataFormatException.invalidField("email", "Invalid email format: $trimmed")
            }
        }

        private fun validateTel(tel: String) {
            val trimmed = tel.trim()
            if (!TEL_REGEX.matches(trimmed)) {
                throw InvalidDataFormatException.invalidField("tel", "Invalid phone number format: $trimmed")
            }
        }
    }
}

