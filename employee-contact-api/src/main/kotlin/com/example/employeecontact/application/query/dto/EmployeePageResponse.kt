package com.example.employeecontact.application.query.dto

data class  EmployeePageResponse(
    val content: List<EmployeeResponse>,
    val page: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
)
