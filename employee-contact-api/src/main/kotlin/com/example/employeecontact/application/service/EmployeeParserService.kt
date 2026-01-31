package com.example.employeecontact.application.service

import com.example.employeecontact.application.command.dto.CreateEmployeeCommand
import com.example.employeecontact.domain.exception.InvalidDataFormatException
import org.springframework.stereotype.Service

@Service
class EmployeeParserService(
    private val parsers: List<EmployeeParser>
) {

    fun parse(content: String, contentType: String?, filename: String?): List<CreateEmployeeCommand> {
        val parser = findParser(contentType, filename, content)
        return parser.parse(content)
    }

    private fun findParser(contentType: String?, filename: String?, content: String): EmployeeParser {
        // First try to find by contentType or filename
        parsers.find { it.canParse(contentType, filename) }?.let { return it }

        // Fallback: auto-detect by content
        parsers.find { it.canParseContent(content) }?.let { return it }

        throw InvalidDataFormatException("Unsupported file format")
    }
}
