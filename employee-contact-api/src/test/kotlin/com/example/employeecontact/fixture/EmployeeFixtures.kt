package com.example.employeecontact.fixture

import com.example.employeecontact.application.command.dto.CreateEmployeeCommand
import com.example.employeecontact.domain.model.Employee
import java.time.LocalDate
import java.time.LocalDateTime

object EmployeeFixtures {

    fun createEmployee(
        id: Long = 1L,
        name: String = "홍길동",
        email: String = "hong@example.com",
        tel: String = "01012345678",
        joined: LocalDate = LocalDate.of(2020, 1, 15),
        createdAt: LocalDateTime = LocalDateTime.now()
    ) = Employee(
        id = id,
        name = name,
        email = email,
        tel = tel,
        joined = joined,
        createdAt = createdAt
    )

    fun createCommand(
        name: String = "홍길동",
        email: String = "hong@example.com",
        tel: String = "010-1234-5678",
        joined: LocalDate = LocalDate.of(2020, 1, 15)
    ) = CreateEmployeeCommand(
        name = name,
        email = email,
        tel = tel,
        joined = joined
    )

    fun sampleEmployees() = listOf(
        createEmployee(id = 1L, name = "김철수", email = "charles@example.com", tel = "01075312468", joined = LocalDate.of(2018, 3, 7)),
        createEmployee(id = 2L, name = "박영희", email = "matilda@example.com", tel = "01087654321", joined = LocalDate.of(2021, 4, 28)),
        createEmployee(id = 3L, name = "홍길동", email = "kildong@example.com", tel = "01012345678", joined = LocalDate.of(2015, 8, 15))
    )

    const val VALID_CSV = """김철수, charles@example.com, 01075312468, 2018.03.07
박영희, matilda@example.com, 01087654321, 2021.04.28
홍길동, kildong@example.com, 01012345678, 2015.08.15"""

    const val VALID_JSON = """[
  {"name":"김클로", "email":"clo@example.com", "tel":"010-1111-2424", "joined":"2012-01-05"},
  {"name":"박마블", "email":"md@example.com", "tel":"010-3535-7979", "joined":"2013-07-01"},
  {"name":"홍커넥", "email":"connect@example.com", "tel":"010-8531-7942", "joined":"2019-12-05"}
]"""

    const val INVALID_CSV = """김철수, charles@example.com"""

    const val INVALID_JSON = """{"invalid": "json"}"""
}
