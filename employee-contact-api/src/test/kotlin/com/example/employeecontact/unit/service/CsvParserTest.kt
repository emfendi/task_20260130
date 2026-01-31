package com.example.employeecontact.unit.service

import com.example.employeecontact.application.service.CsvParser
import com.example.employeecontact.domain.exception.InvalidDataFormatException
import com.example.employeecontact.fixture.EmployeeFixtures
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class CsvParserTest {

    private lateinit var csvParser: CsvParser

    @BeforeEach
    fun setUp() {
        csvParser = CsvParser()
    }

    @Test
    @DisplayName("유효한 CSV 내용을 파싱할 수 있다")
    fun `should parse valid CSV content`() {
        // given
        val csvContent = EmployeeFixtures.VALID_CSV

        // when
        val result = csvParser.parse(csvContent)

        // then
        assertEquals(3, result.size)
        assertEquals("김철수", result[0].name)
        assertEquals("charles@example.com", result[0].email)
        assertEquals("01075312468", result[0].tel)
        assertEquals(LocalDate.of(2018, 3, 7), result[0].joined)
    }

    @Test
    @DisplayName("공백이 포함된 CSV를 처리할 수 있다")
    fun `should handle CSV with extra whitespace`() {
        // given
        val csvContent = "  홍길동  ,  hong@example.com  ,  010-1234-5678  ,  2020.01.15  "

        // when
        val result = csvParser.parse(csvContent)

        // then
        assertEquals(1, result.size)
        assertEquals("홍길동", result[0].name)
        assertEquals("hong@example.com", result[0].email)
    }

    @Test
    @DisplayName("빈 줄이 포함된 CSV를 처리할 수 있다")
    fun `should skip blank lines`() {
        // given
        val csvContent = """
            홍길동, hong@example.com, 01012345678, 2020.01.15

            김철수, kim@example.com, 01098765432, 2019.05.20
        """.trimIndent()

        // when
        val result = csvParser.parse(csvContent)

        // then
        assertEquals(2, result.size)
    }

    @Test
    @DisplayName("필드가 부족한 CSV는 예외를 발생시킨다")
    fun `should throw exception for insufficient fields`() {
        // given
        val csvContent = EmployeeFixtures.INVALID_CSV

        // when & then
        val exception = assertThrows<InvalidDataFormatException> {
            csvParser.parse(csvContent)
        }
        assertTrue(exception.message!!.contains("Expected 4 fields"))
    }

    @Test
    @DisplayName("빈 내용은 예외를 발생시킨다")
    fun `should throw exception for empty content`() {
        // given
        val csvContent = ""

        // when & then
        val exception = assertThrows<InvalidDataFormatException> {
            csvParser.parse(csvContent)
        }
        assertTrue(exception.message!!.contains("Empty content"))
    }

    @Test
    @DisplayName("잘못된 날짜 형식은 예외를 발생시킨다")
    fun `should throw exception for invalid date format`() {
        // given
        val csvContent = "홍길동, hong@example.com, 01012345678, 2020/13/45"

        // when & then
        assertThrows<InvalidDataFormatException> {
            csvParser.parse(csvContent)
        }
    }

    @Test
    @DisplayName("지원하지 않는 날짜 형식은 예외를 발생시킨다")
    fun `should throw exception for unsupported date format`() {
        // given - dd/MM/yyyy 형식은 지원하지 않음
        val csvContent = "홍길동, hong@example.com, 01012345678, 15/01/2020"

        // when & then
        val exception = assertThrows<InvalidDataFormatException> {
            csvParser.parse(csvContent)
        }
        assertTrue(exception.message!!.contains("Invalid date format"))
    }

    @Test
    @DisplayName("날짜가 아닌 문자열은 예외를 발생시킨다")
    fun `should throw exception for non-date string`() {
        // given
        val csvContent = "홍길동, hong@example.com, 01012345678, not-a-date"

        // when & then
        val exception = assertThrows<InvalidDataFormatException> {
            csvParser.parse(csvContent)
        }
        assertTrue(exception.message!!.contains("Invalid date format"))
    }

    @Test
    @DisplayName("CSV 콘텐츠 타입을 식별할 수 있다")
    fun `should identify CSV content type`() {
        assertTrue(csvParser.canParse("text/csv", null))
        assertTrue(csvParser.canParse("text/plain", null))
        assertTrue(csvParser.canParse(null, "employees.csv"))
        assertFalse(csvParser.canParse("application/json", null))
        assertFalse(csvParser.canParse(null, "employees.json"))
    }
}
