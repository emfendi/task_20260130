package com.example.employeecontact.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Employee(
    val id: Long,
    val name: String,
    val email: String,
    val tel: String,
    val joined: LocalDate,
    val createdAt: LocalDateTime,
)
