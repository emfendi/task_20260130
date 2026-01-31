package com.example.employeecontact.application.service

import com.example.employeecontact.application.command.dto.CreateEmployeeCommand
import com.example.employeecontact.domain.exception.InvalidDataFormatException
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CsvParser : EmployeeParser {

    override fun parse(content: String): List<CreateEmployeeCommand> {
        logger.debug { "Parsing CSV content" }

        val lines = content.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (lines.isEmpty()) {
            throw InvalidDataFormatException.invalidCsv("Empty content")
        }

        return lines.mapIndexed { index, line ->
            try {
                parseLine(line)
            } catch (e: Exception) {
                logger.error { "Failed to parse line ${index + 1}: $line" }
                throw InvalidDataFormatException.invalidCsv("Error at line ${index + 1}: ${e.message}")
            }
        }.also {
            logger.info { "Successfully parsed ${it.size} records from CSV" }
        }
    }

    private fun parseLine(line: String): CreateEmployeeCommand {
        val parts = line.split(",").map { it.trim() }

        if (parts.size < 4) {
            throw InvalidDataFormatException.invalidCsv(
                "Expected 4 fields (name, email, tel, joined), got ${parts.size}"
            )
        }

        val (name, email, tel, joined) = parts

        return CreateEmployeeCommand(
            name = name,
            email = email,
            tel = tel,
            joined = DateParser.parse(joined)
        )
    }

    override fun canParse(contentType: String?, filename: String?): Boolean {
        return contentType?.contains("csv", ignoreCase = true) == true ||
                contentType?.contains("text/plain", ignoreCase = true) == true ||
                filename?.endsWith(".csv", ignoreCase = true) == true
    }

    override fun canParseContent(content: String): Boolean {
        val trimmed = content.trim()
        return !trimmed.startsWith("[") && !trimmed.startsWith("{")
    }
}
