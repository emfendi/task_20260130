package com.example.employeecontact.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "직원 등록 응답")
data class CreateEmployeeResponse(
    @Schema(description = "등록된 직원 수", example = "3")
    val count: Int
)
