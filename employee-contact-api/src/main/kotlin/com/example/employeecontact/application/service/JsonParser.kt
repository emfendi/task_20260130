package com.example.employeecontact.application.service

import com.example.employeecontact.application.command.dto.CreateEmployeeCommand
import com.example.employeecontact.application.service.dto.EmployeeJsonDto
import com.example.employeecontact.domain.exception.InvalidDataFormatException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class JsonParser(
    private val objectMapper: ObjectMapper
) : EmployeeParser {

    override fun parse(content: String): List<CreateEmployeeCommand> {
        logger.debug { "Parsing JSON content" }

        val trimmedContent = content.trim()

        if (trimmedContent.isBlank()) {
            throw InvalidDataFormatException.invalidJson("Empty content")
        }

        return try {
            val employeeJsonDtoList: List<EmployeeJsonDto> = if (trimmedContent.startsWith("[")) {
                objectMapper.readValue(trimmedContent)
            } else {
                listOf(objectMapper.readValue(trimmedContent))
            }

            employeeJsonDtoList.map { dto ->
                CreateEmployeeCommand(
                    name = dto.name.trim(),
                    email = dto.email.trim(),
                    tel = dto.tel.trim(),
                    joined = DateParser.parse(dto.joined)
                )
            }.also {
                logger.info { "Successfully parsed ${it.size} records from JSON" }
            }
        } catch (e: JsonProcessingException) {
            logger.error { "Failed to parse JSON: ${e.message}" }
            throw InvalidDataFormatException.invalidJson(e.message ?: "Parse error")
        }
    }

    override fun canParse(contentType: String?, filename: String?): Boolean {
        return contentType?.contains("json", ignoreCase = true) == true ||
                filename?.endsWith(".json", ignoreCase = true) == true
    }

    override fun canParseContent(content: String): Boolean {
        val trimmed = content.trim()
        return trimmed.startsWith("[") || trimmed.startsWith("{")
    }
}
