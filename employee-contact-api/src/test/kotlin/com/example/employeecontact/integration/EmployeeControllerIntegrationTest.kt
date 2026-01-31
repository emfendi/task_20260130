package com.example.employeecontact.integration

import com.example.employeecontact.fixture.EmployeeFixtures
import com.example.employeecontact.infrastructure.entity.EmployeeEntity
import com.example.employeecontact.infrastructure.persistence.EmployeeQueryRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var employeeRepository: EmployeeQueryRepository

    companion object {
        private const val API_KEY_HEADER = "X-API-Key"
        private const val API_KEY = "test-api-key"
    }

    @BeforeEach
    fun setUp() {
        employeeRepository.deleteAll()
    }

    @Test
    @DisplayName("GET /api/employee - 페이징된 직원 목록을 반환한다")
    fun `should return paginated employees`() {
        // given
        val employees = EmployeeFixtures.sampleEmployees().map {
            EmployeeEntity(
                name = it.name,
                email = it.email,
                tel = it.tel,
                joined = it.joined
            )
        }
        employeeRepository.saveAll(employees)

        // when & then
        mockMvc.perform(
            get("/api/employee")
                .header(API_KEY_HEADER, API_KEY)
                .param("page", "0")
                .param("pageSize", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(3))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.pageSize").value(10))
            .andExpect(jsonPath("$.totalElements").value(3))
    }

    @Test
    @DisplayName("GET /api/employee/{name} - 이름으로 직원을 조회한다")
    fun `should return employees by name`() {
        // given
        employeeRepository.save(
            EmployeeEntity(name = "홍길동", email = "hong@example.com", tel = "01012345678", joined = LocalDate.of(2020, 1, 15))
        )

        // when & then
        mockMvc.perform(
            get("/api/employee/홍길동")
                .header(API_KEY_HEADER, API_KEY)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("홍길동"))
            .andExpect(jsonPath("$[0].email").value("hong@example.com"))
    }

    @Test
    @DisplayName("GET /api/employee/{name} - 동명이인 모두 반환")
    fun `should return all employees with same name`() {
        // given
        employeeRepository.saveAll(
            listOf(
                EmployeeEntity(name = "홍길동", email = "hong1@example.com", tel = "01011111111", joined = LocalDate.of(2020, 1, 15)),
                EmployeeEntity(name = "홍길동", email = "hong2@example.com", tel = "01022222222", joined = LocalDate.of(2021, 5, 20))
            )
        )

        // when & then
        mockMvc.perform(
            get("/api/employee/홍길동")
                .header(API_KEY_HEADER, API_KEY)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    @DisplayName("POST /api/employee - CSV 파일로 직원을 생성한다")
    fun `should create employees from CSV file`() {
        // given
        val csvFile = MockMultipartFile(
            "file",
            "employees.csv",
            "text/csv",
            EmployeeFixtures.VALID_CSV.toByteArray()
        )

        // when & then
        mockMvc.perform(
            multipart("/api/employee")
                .file(csvFile)
                .header(API_KEY_HEADER, API_KEY)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.count").value(3))
    }

    @Test
    @DisplayName("POST /api/employee - JSON 파일로 직원을 생성한다")
    fun `should create employees from JSON file`() {
        // given
        val jsonFile = MockMultipartFile(
            "file",
            "employees.json",
            "application/json",
            EmployeeFixtures.VALID_JSON.toByteArray()
        )

        // when & then
        mockMvc.perform(
            multipart("/api/employee")
                .file(jsonFile)
                .header(API_KEY_HEADER, API_KEY)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.count").value(3))
    }

    @Test
    @DisplayName("POST /api/employee - JSON Body로 직원을 생성한다")
    fun `should create employees from JSON body`() {
        // given
        val jsonBody = """[
            {"name":"테스트","email":"test@example.com","tel":"010-1234-5678","joined":"2024-01-01"}
        ]"""

        // when & then
        mockMvc.perform(
            post("/api/employee")
                .header(API_KEY_HEADER, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.count").value(1))
    }

    @Test
    @DisplayName("POST /api/employee - CSV Body로 직원을 생성한다")
    fun `should create employees from CSV body`() {
        // given
        val csvBody = "테스트, test@example.com, 01012345678, 2024.01.01"

        // when & then
        mockMvc.perform(
            post("/api/employee")
                .header(API_KEY_HEADER, API_KEY)
                .contentType("text/csv")
                .content(csvBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.count").value(1))
    }

    @Test
    @DisplayName("POST /api/employee - 잘못된 CSV는 400 에러를 반환한다")
    fun `should return 400 for invalid CSV`() {
        // given
        val invalidCsv = "김철수, invalid"

        // when & then
        mockMvc.perform(
            post("/api/employee")
                .header(API_KEY_HEADER, API_KEY)
                .contentType("text/csv")
                .content(invalidCsv)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("GET /api/employee - 잘못된 페이지 파라미터는 400 에러를 반환한다")
    fun `should return 400 for invalid page parameter`() {
        // when & then
        mockMvc.perform(
            get("/api/employee")
                .header(API_KEY_HEADER, API_KEY)
                .param("page", "-1")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("API Key 없이 요청하면 401 에러를 반환한다")
    fun `should return 401 without API key`() {
        // when & then
        mockMvc.perform(get("/api/employee"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("잘못된 API Key로 요청하면 401 에러를 반환한다")
    fun `should return 401 with invalid API key`() {
        // when & then
        mockMvc.perform(
            get("/api/employee")
                .header(API_KEY_HEADER, "wrong-key")
        )
            .andExpect(status().isUnauthorized)
    }
}
