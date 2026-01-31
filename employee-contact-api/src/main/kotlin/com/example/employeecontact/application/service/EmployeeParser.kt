package com.example.employeecontact.application.service

import com.example.employeecontact.application.command.dto.CreateEmployeeCommand

interface EmployeeParser {
    fun parse(content: String): List<CreateEmployeeCommand>
    fun canParse(contentType: String?, filename: String?): Boolean
    fun canParseContent(content: String): Boolean = false
}
