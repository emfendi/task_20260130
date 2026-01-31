package com.example.employeecontact.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Employee Contact API")
                    .version("1.0.0")
                    .description(
                        """
                        직원 긴급 연락망 관리 API

                        ## 기능
                        - 직원 연락처 목록 조회 (페이징)
                        - 이름으로 직원 검색
                        - CSV/JSON 파일 또는 데이터로 직원 등록

                        ## 지원 형식
                        - CSV: `이름, 이메일, 전화번호, 입사일(yyyy.MM.dd)`
                        - JSON: `{"name": "...", "email": "...", "tel": "...", "joined": "yyyy-MM-dd"}`
                        """.trimIndent()
                    )
                    .contact(
                        Contact()
                            .name("API Support")
                            .email("support@example.com")
                    )
            )
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server")
                )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "ApiKeyAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .name("X-API-Key")
                            .description("API Key for authentication")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList("ApiKeyAuth"))
    }
}
