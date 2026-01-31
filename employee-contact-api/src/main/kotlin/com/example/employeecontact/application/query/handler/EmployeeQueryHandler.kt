package com.example.employeecontact.application.query.handler

import com.example.employeecontact.application.query.dto.EmployeePageResponse
import com.example.employeecontact.application.query.dto.EmployeeResponse
import com.example.employeecontact.infrastructure.persistence.EmployeeQueryRepository
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class EmployeeQueryHandler(
    private val repository: EmployeeQueryRepository
) {

    fun findAll(page: Int, pageSize: Int): EmployeePageResponse {
        logger.debug { "Querying all employees: page=$page, pageSize=$pageSize" }

        val pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "id"))
        val employeePage = repository.findAll(pageable)

        return EmployeePageResponse(
            content = employeePage.content.map { EmployeeResponse.from(it.toModel()) },
            page = employeePage.number,
            pageSize = employeePage.size,
            totalElements = employeePage.totalElements,
            totalPages = employeePage.totalPages,
        ).also {
            logger.debug { "Query result: ${it.content.size} employees, total=${it.totalElements}" }
        }
    }

    fun findByName(name: String): List<EmployeeResponse> {
        logger.debug { "Querying employees by name: $name" }

        val employees = repository.findByName(name)

        return employees.map { EmployeeResponse.from(it.toModel()) }.also {
            logger.debug { "Found ${it.size} employee(s) with name: $name" }
        }
    }
}
