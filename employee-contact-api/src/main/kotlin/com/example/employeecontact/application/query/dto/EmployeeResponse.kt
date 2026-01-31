package com.example.employeecontact.application.query.dto

import com.example.employeecontact.domain.model.Employee
import java.time.LocalDate

data class EmployeeResponse(
    val id: Long,
    val name: String,
    val email: String,
    val tel: String,
    val joined: LocalDate
) {
    companion object {
        fun from(employee: Employee): EmployeeResponse {
            return EmployeeResponse(
                id = requireNotNull(employee.id),
                name = employee.name,
                email = employee.email,
                tel = employee.tel,
                joined = employee.joined
            )
        }
    }
}
