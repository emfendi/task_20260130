package com.example.employeecontact.presentation.dto

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null
)
