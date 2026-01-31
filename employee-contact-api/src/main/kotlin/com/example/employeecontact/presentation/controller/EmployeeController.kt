package com.example.employeecontact.presentation.controller

import com.example.employeecontact.application.command.dto.CreateEmployeeCommand
import com.example.employeecontact.application.command.handler.CreateEmployeeCommandHandler
import com.example.employeecontact.application.query.dto.EmployeePageResponse
import com.example.employeecontact.application.query.dto.EmployeeResponse
import com.example.employeecontact.application.query.handler.EmployeeQueryHandler
import com.example.employeecontact.application.service.EmployeeParserService
import com.example.employeecontact.presentation.dto.CreateEmployeeRequest
import com.example.employeecontact.presentation.dto.CreateEmployeeResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/employee")
@Tag(name = "Employee", description = "Employee Contact Management API")
@Validated
class EmployeeController(
    private val commandHandler: CreateEmployeeCommandHandler,
    private val queryHandler: EmployeeQueryHandler,
    private val parserResolver: EmployeeParserService
) {

    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieve all employees with pagination support")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved employees"),
            ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
        ]
    )
    fun getAllEmployees(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @Parameter(description = "Number of items per page", example = "10")
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) pageSize: Int
    ): ResponseEntity<EmployeePageResponse> {
        val response = queryHandler.findAll(page, pageSize)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get employees by name", description = "Retrieve all employees matching the given name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved employees"),
            ApiResponse(responseCode = "404", description = "No employees found with the given name")
        ]
    )
    fun getEmployeeByName(
        @Parameter(description = "Employee name to search", example = "홍길동")
        @PathVariable name: String
    ): ResponseEntity<List<EmployeeResponse>> {
        val employees = queryHandler.findByName(name)
        return ResponseEntity.ok(employees)
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Create employees from file", description = "Upload CSV or JSON file to create employees")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Employees created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid file format or content")
        ]
    )
    fun createEmployeesFromFile(
        @Parameter(description = "CSV or JSON file containing employee data")
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<CreateEmployeeResponse> {
        val content = file.inputStream.bufferedReader(StandardCharsets.UTF_8).readText()
        val commands = parserResolver.parse(content, file.contentType, file.originalFilename)

        val count = commandHandler.handleBatch(commands)

        return ResponseEntity.status(HttpStatus.CREATED).body(CreateEmployeeResponse(count))
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Create employees from JSON body", description = "Create employees from JSON data in request body")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Employees created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid JSON format or validation error")
        ]
    )
    fun createEmployeesFromJson(
        @Valid @RequestBody requests: List<CreateEmployeeRequest>
    ): ResponseEntity<CreateEmployeeResponse> {
        val commands = requests.map { request ->
            CreateEmployeeCommand(
                name = request.name,
                email = request.email,
                tel = request.tel,
                joined = request.joined
            )
        }

        val count = commandHandler.handleBatch(commands)

        return ResponseEntity.status(HttpStatus.CREATED).body(CreateEmployeeResponse(count))
    }

    @PostMapping(consumes = [MediaType.TEXT_PLAIN_VALUE, "text/csv"])
    @Operation(summary = "Create employees from CSV body", description = "Create employees from CSV data in request body")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Employees created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid CSV format")
        ]
    )
    fun createEmployeesFromCsvBody(
        @RequestBody csvContent: String
    ): ResponseEntity<CreateEmployeeResponse> {
        val commands = parserResolver.parse(csvContent, "text/csv", null)
        val count = commandHandler.handleBatch(commands)

        return ResponseEntity.status(HttpStatus.CREATED).body(CreateEmployeeResponse(count))
    }
}
