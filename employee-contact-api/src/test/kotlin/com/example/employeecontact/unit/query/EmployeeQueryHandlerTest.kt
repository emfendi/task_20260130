package com.example.employeecontact.unit.query

import com.example.employeecontact.application.query.handler.EmployeeQueryHandler
import com.example.employeecontact.fixture.EmployeeFixtures
import com.example.employeecontact.infrastructure.entity.EmployeeEntity
import com.example.employeecontact.infrastructure.persistence.EmployeeQueryRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class EmployeeQueryHandlerTest {

    private lateinit var repository: EmployeeQueryRepository
    private lateinit var queryHandler: EmployeeQueryHandler

    @BeforeEach
    fun setUp() {
        repository = mockk()
        queryHandler = EmployeeQueryHandler(repository)
    }

    @Test
    @DisplayName("페이징된 직원 목록을 반환한다")
    fun `should return paginated results`() {
        // given
        val entities = EmployeeFixtures.sampleEmployees().map { emp ->
            EmployeeEntity(
                id = emp.id,
                name = emp.name,
                email = emp.email,
                tel = emp.tel,
                joined = emp.joined,
                createdAt = emp.createdAt
            )
        }
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))
        val page = PageImpl(entities, pageable, entities.size.toLong())

        every { repository.findAll(any<Pageable>()) } returns page

        // when
        val result = queryHandler.findAll(0, 10)

        // then
        assertEquals(3, result.content.size)
        assertEquals(0, result.page)
        assertEquals(10, result.pageSize)
        assertEquals(3, result.totalElements)
        assertEquals(1, result.totalPages)
    }

    @Test
    @DisplayName("이름으로 직원을 검색하여 모든 동명이인을 반환한다")
    fun `should return all employees with matching name`() {
        // given
        val entities = listOf(
            EmployeeEntity(id = 1L, name = "홍길동", email = "hong1@example.com", tel = "01011111111", joined = java.time.LocalDate.now()),
            EmployeeEntity(id = 2L, name = "홍길동", email = "hong2@example.com", tel = "01022222222", joined = java.time.LocalDate.now())
        )

        every { repository.findByName("홍길동") } returns entities

        // when
        val result = queryHandler.findByName("홍길동")

        // then
        assertEquals(2, result.size)
        assertTrue(result.all { it.name == "홍길동" })
        assertEquals("hong1@example.com", result[0].email)
        assertEquals("hong2@example.com", result[1].email)
    }

    @Test
    @DisplayName("일치하는 이름이 없으면 빈 목록을 반환한다")
    fun `should return empty list when no match found`() {
        // given
        every { repository.findByName("존재하지않는이름") } returns emptyList()

        // when
        val result = queryHandler.findByName("존재하지않는이름")

        // then
        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName("빈 데이터베이스에서 빈 페이지를 반환한다")
    fun `should return empty page when database is empty`() {
        // given
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))
        val emptyPage = PageImpl<EmployeeEntity>(emptyList(), pageable, 0)

        every { repository.findAll(any<Pageable>()) } returns emptyPage

        // when
        val result = queryHandler.findAll(0, 10)

        // then
        assertTrue(result.content.isEmpty())
        assertEquals(0, result.totalElements)
        assertEquals(0, result.totalPages)
    }
}
