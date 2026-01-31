package com.example.employeecontact.unit.service

import com.example.employeecontact.application.service.JsonParser
import com.example.employeecontact.domain.exception.InvalidDataFormatException
import com.example.employeecontact.fixture.EmployeeFixtures
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class JsonParserTest {

    private lateinit var jsonParser: JsonParser
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        objectMapper = jacksonObjectMapper()
        jsonParser = JsonParser(objectMapper)
    }

    @Test
    @DisplayName("유효한 JSON 배열을 파싱할 수 있다")
    fun `should parse valid JSON array`() {
        // given
        val jsonContent = EmployeeFixtures.VALID_JSON

        // when
        val result = jsonParser.parse(jsonContent)

        // then
        assertEquals(3, result.size)
        assertEquals("김클로", result[0].name)
        assertEquals("clo@example.com", result[0].email)
        assertEquals("010-1111-2424", result[0].tel)
        assertEquals(LocalDate.of(2012, 1, 5), result[0].joined)
    }

    @Test
    @DisplayName("단일 JSON 객체를 파싱할 수 있다")
    fun `should parse single JSON object`() {
        // given
        val jsonContent = """{"name":"홍길동", "email":"hong@example.com", "tel":"010-1234-5678", "joined":"2020-01-15"}"""

        // when
        val result = jsonParser.parse(jsonContent)

        // then
        assertEquals(1, result.size)
        assertEquals("홍길동", result[0].name)
        assertEquals("hong@example.com", result[0].email)
    }

    @Test
    @DisplayName("하이픈이 있는 전화번호를 처리할 수 있다")
    fun `should handle phone numbers with dashes`() {
        // given
        val jsonContent = """[{"name":"테스트", "email":"test@example.com", "tel":"010-9999-8888", "joined":"2023-06-15"}]"""

        // when
        val result = jsonParser.parse(jsonContent)

        // then
        assertEquals("010-9999-8888", result[0].tel)
    }

    @Test
    @DisplayName("빈 내용은 예외를 발생시킨다")
    fun `should throw exception for empty content`() {
        // given
        val jsonContent = ""

        // when & then
        val exception = assertThrows<InvalidDataFormatException> {
            jsonParser.parse(jsonContent)
        }
        assertTrue(exception.message!!.contains("Empty content"))
    }

    @Test
    @DisplayName("잘못된 JSON 형식은 예외를 발생시킨다")
    fun `should throw exception for invalid JSON format`() {
        // given
        val jsonContent = "{ invalid json }"

        // when & then
        assertThrows<InvalidDataFormatException> {
            jsonParser.parse(jsonContent)
        }
    }

    @Test
    @DisplayName("필수 필드가 누락된 JSON은 예외를 발생시킨다")
    fun `should throw exception for missing required fields`() {
        // given
        val jsonContent = """[{"name":"홍길동"}]"""

        // when & then
        assertThrows<InvalidDataFormatException> {
            jsonParser.parse(jsonContent)
        }
    }

    @Test
    @DisplayName("JSON 콘텐츠 타입을 식별할 수 있다")
    fun `should identify JSON content type`() {
        assertTrue(jsonParser.canParse("application/json", null))
        assertTrue(jsonParser.canParse(null, "employees.json"))
        assertFalse(jsonParser.canParse("text/csv", null))
        assertFalse(jsonParser.canParse(null, "employees.csv"))
    }

    @Test
    @DisplayName("잘못된 날짜 형식은 예외를 발생시킨다")
    fun `should throw exception for invalid date format`() {
        // given
        val jsonContent = """[{"name":"홍길동", "email":"hong@example.com", "tel":"010-1234-5678", "joined":"2020/13/45"}]"""

        // when & then
        assertThrows<InvalidDataFormatException> {
            jsonParser.parse(jsonContent)
        }
    }

    @Test
    @DisplayName("지원하지 않는 날짜 형식은 예외를 발생시킨다")
    fun `should throw exception for unsupported date format`() {
        // given - dd/MM/yyyy 형식은 지원하지 않음
        val jsonContent = """[{"name":"홍길동", "email":"hong@example.com", "tel":"010-1234-5678", "joined":"15/01/2020"}]"""

        // when & then
        val exception = assertThrows<InvalidDataFormatException> {
            jsonParser.parse(jsonContent)
        }
        assertTrue(exception.message!!.contains("Invalid date format"))
    }

    @Test
    @DisplayName("날짜가 아닌 문자열은 예외를 발생시킨다")
    fun `should throw exception for non-date string`() {
        // given
        val jsonContent = """[{"name":"홍길동", "email":"hong@example.com", "tel":"010-1234-5678", "joined":"not-a-date"}]"""

        // when & then
        val exception = assertThrows<InvalidDataFormatException> {
            jsonParser.parse(jsonContent)
        }
        assertTrue(exception.message!!.contains("Invalid date format"))
    }
}
