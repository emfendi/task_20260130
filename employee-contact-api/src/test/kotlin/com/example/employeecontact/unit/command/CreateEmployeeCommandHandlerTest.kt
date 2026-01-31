package com.example.employeecontact.unit.command

import com.example.employeecontact.application.command.handler.CreateEmployeeCommandHandler
import com.example.employeecontact.domain.exception.InvalidDataFormatException
import com.example.employeecontact.fixture.EmployeeFixtures
import com.example.employeecontact.infrastructure.entity.EmployeeEntity
import com.example.employeecontact.infrastructure.persistence.EmployeeCommandRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CreateEmployeeCommandHandlerTest {

    private lateinit var commandRepository: EmployeeCommandRepository
    private lateinit var commandHandler: CreateEmployeeCommandHandler

    @BeforeEach
    fun setUp() {
        commandRepository = mockk()
        commandHandler = CreateEmployeeCommandHandler(commandRepository)
    }

    @Test
    @DisplayName("배치로 여러 직원을 생성할 수 있다")
    fun `should batch create multiple employees`() {
        // given
        val commands = listOf(
            EmployeeFixtures.createCommand(name = "홍길동", email = "hong@example.com"),
            EmployeeFixtures.createCommand(name = "김철수", email = "kim@example.com"),
            EmployeeFixtures.createCommand(name = "박영희", email = "park@example.com")
        )

        val entitiesSlot = slot<List<EmployeeEntity>>()

        every { commandRepository.saveAll(capture(entitiesSlot)) } returns 3

        // when
        val result = commandHandler.handleBatch(commands)

        // then
        assertEquals(3, result)
        verify(exactly = 1) { commandRepository.saveAll(any()) }
    }

    @Test
    @DisplayName("전화번호의 하이픈이 제거된다")
    fun `should normalize phone number by removing dashes`() {
        // given
        val command = EmployeeFixtures.createCommand(tel = "010-1234-5678")
        val entitiesSlot = slot<List<EmployeeEntity>>()

        every { commandRepository.saveAll(capture(entitiesSlot)) } returns 1

        // when
        commandHandler.handleBatch(listOf(command))

        // then
        assertEquals("01012345678", entitiesSlot.captured[0].tel)
    }

    @Test
    @DisplayName("빈 이름은 명령 생성시 예외를 발생시킨다")
    fun `should throw exception for blank name`() {
        // when & then
        assertThrows(InvalidDataFormatException::class.java) {
            EmployeeFixtures.createCommand(name = "")
        }
    }

    @Test
    @DisplayName("빈 이메일은 명령 생성시 예외를 발생시킨다")
    fun `should throw exception for blank email`() {
        // when & then
        assertThrows(InvalidDataFormatException::class.java) {
            EmployeeFixtures.createCommand(email = "")
        }
    }
}
