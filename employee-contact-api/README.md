# Employee Contact API

직원 긴급 연락망 관리 API

## 기술 스택

- **Framework**: Spring Boot 3.2.2
- **Language**: Kotlin 1.9.22
- **JDK**: 17
- **Database**: H2 (In-Memory)
- **Security**: API Key Authentication
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Build Tool**: Gradle (Kotlin DSL)

## 프로젝트 구조

```
src/main/kotlin/com/example/employeecontact/
├── EmployeeContactApplication.kt           # 메인 애플리케이션
├── config/                                  # 설정
│   ├── OpenApiConfig.kt                    # Swagger 설정
│   ├── SecurityConfig.kt                   # 보안 설정
│   └── ApiKeyAuthFilter.kt                 # API Key 인증 필터
├── domain/                                  # 도메인 레이어
│   ├── model/Employee.kt                   # 순수 도메인 모델
│   └── exception/                          # 도메인 예외
│       └── InvalidDataFormatException.kt
├── application/                             # 애플리케이션 레이어 (CQRS)
│   ├── command/                            # 쓰기 작업
│   │   ├── dto/CreateEmployeeCommand.kt   # 커맨드 (with validation)
│   │   └── handler/CreateEmployeeCommandHandler.kt
│   ├── query/                              # 읽기 작업
│   │   ├── dto/EmployeeResponse.kt
│   │   ├── dto/EmployeePageResponse.kt
│   │   └── handler/EmployeeQueryHandler.kt
│   └── service/                            # 파싱 서비스
│       ├── EmployeeParser.kt              # 파서 인터페이스
│       ├── EmployeeParserService.kt       # 파서 선택/실행
│       ├── CsvParser.kt                   # CSV 파서
│       ├── JsonParser.kt                  # JSON 파서
│       ├── DateParser.kt                  # 날짜 파서
│       └── dto/EmployeeJsonDto.kt
├── infrastructure/                          # 인프라 레이어
│   ├── entity/EmployeeEntity.kt           # JPA 엔티티
│   ├── persistence/
│   │   ├── EmployeeCommandRepository.kt   # 쓰기 전용 레포지토리
│   │   └── EmployeeQueryRepository.kt     # 읽기 전용 레포지토리
│   └── logging/LoggingAspect.kt           # AOP 로깅
└── presentation/                            # 프레젠테이션 레이어
    ├── controller/EmployeeController.kt    # REST API
    ├── dto/
    │   ├── CreateEmployeeRequest.kt       # 요청 DTO
    │   └── ErrorResponse.kt               # 에러 응답
    └── advice/GlobalExceptionHandler.kt    # 예외 처리
```

## 빌드 및 실행

### 요구사항

- JDK 17 이상
- Gradle 8.x (Wrapper 포함)

### 빌드

```bash
cd employee-contact-api
./gradlew build
```

### 실행

```bash
./gradlew bootRun
```

서버 실행 후: http://localhost:8080

### 테스트

```bash
./gradlew test
```

## 인증

모든 API 요청에는 API Key가 필요합니다.

```http
X-API-Key: dev-api-key-change-in-production
```

> 환경 변수 `API_KEY`로 커스텀 키 설정 가능

**예외 경로** (인증 불필요):
- `/swagger-ui/**` - Swagger UI
- `/api-docs/**` - OpenAPI 스펙
- `/h2-console/**` - H2 콘솔
- `/actuator/health` - 헬스 체크

## API 명세

### 1. 직원 목록 조회 (페이징)

```http
GET /api/employee?page={page}&pageSize={pageSize}
X-API-Key: dev-api-key-change-in-production
```

**Parameters:**
- `page` (optional, default: 0): 페이지 번호 (0부터 시작, 최소: 0)
- `pageSize` (optional, default: 10): 페이지당 항목 수 (1-100)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "홍길동",
      "email": "hong@example.com",
      "tel": "01012345678",
      "joined": "2020-01-15"
    }
  ],
  "page": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### 2. 이름으로 직원 조회

```http
GET /api/employee/{name}
X-API-Key: dev-api-key-change-in-production
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "홍길동",
    "email": "hong@example.com",
    "tel": "01012345678",
    "joined": "2020-01-15"
  }
]
```
> 동명이인이 있을 경우 모두 반환됩니다.

### 3. 직원 등록

#### 3-1. JSON Body

```http
POST /api/employee
Content-Type: application/json
X-API-Key: dev-api-key-change-in-production

[
  {
    "name": "홍길동",
    "email": "hong@example.com",
    "tel": "010-1234-5678",
    "joined": "2020-01-15"
  }
]
```

#### 3-2. CSV Body

```http
POST /api/employee
Content-Type: text/csv
X-API-Key: dev-api-key-change-in-production

홍길동, hong@example.com, 01012345678, 2020.01.15
김철수, kim@example.com, 01098765432, 2019.05.20
```

#### 3-3. 파일 업로드 (CSV/JSON)

```http
POST /api/employee
Content-Type: multipart/form-data
X-API-Key: dev-api-key-change-in-production

file: [employees.csv 또는 employees.json]
```

**Response (201 Created):**
```json
{
  "count": 2
}
```

## 데이터 형식

### CSV 형식
```csv
이름, 이메일, 전화번호, 입사일(yyyy.MM.dd)
```
예시:
```csv
김철수, charles@example.com, 01075312468, 2018.03.07
박영희, matilda@example.com, 01087654321, 2021.04.28
```

### JSON 형식
```json
[
  {
    "name": "이름",
    "email": "이메일",
    "tel": "전화번호",
    "joined": "입사일(yyyy-MM-dd)"
  }
]
```
예시:
```json
[
  {"name":"김클로", "email":"clo@example.com", "tel":"010-1111-2424", "joined":"2012-01-05"}
]
```

### 입력값 검증

| 필드 | 규칙 |
|------|------|
| name | 필수, 최대 100자 |
| email | 필수, 이메일 형식, 중복 불가 |
| tel | 필수, 전화번호 형식 (예: 01012345678, 010-1234-5678) |
| joined | 필수, yyyy-MM-dd 또는 yyyy.MM.dd 또는 yyyy/MM/dd |

### 등록 정책 (All or Nothing)

데이터 등록 시 **전체 성공 또는 전체 실패** 정책을 적용합니다.

- 여러 건의 데이터를 등록할 때, 하나라도 유효성 검증에 실패하면 **전체 등록이 취소**됩니다.
- 부분 성공(일부만 등록)은 지원하지 않습니다.

**예시:**
```json
[
  {"name":"김철수", "email":"kim@example.com", "tel":"010-1234-5678", "joined":"2024-01-15"},
  {"name":"이영희", "email":"invalid-email", "tel":"010-9876-5432", "joined":"2023-06-20"}
]
```
위 요청에서 두 번째 데이터의 이메일이 유효하지 않으면, 첫 번째 데이터도 등록되지 않고 전체 요청이 실패합니다.

## API 문서

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api-docs

## API 테스트

프로젝트 루트의 `requests.http` 파일로 API를 테스트할 수 있습니다.

**지원 도구:**
- IntelliJ IDEA HTTP Client (내장)
- VS Code REST Client 확장

**사용 방법:**
1. `requests.http` 파일 열기
2. 각 요청 옆의 ▶ 버튼 클릭하여 실행
3. 응답 확인

**포함된 테스트:**
- 인증 테스트 (성공/실패)
- 직원 조회 (전체/페이징/이름검색)
- 직원 등록 (JSON/CSV)
- 유효성 검증 실패 케이스

## H2 Console

- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:employeedb`
- **Username**: `sa`
- **Password**: (빈 값)

## 아키텍처

### Clean Architecture + CQRS

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation                         │
│            (Controller, Request/Response DTO)           │
└─────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                     Application                         │
│     ┌─────────────────┐     ┌─────────────────┐        │
│     │    Command      │     │     Query       │        │
│     │  (Write Side)   │     │  (Read Side)    │        │
│     └─────────────────┘     └─────────────────┘        │
│              │ EmployeeParserService │                  │
└─────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                       Domain                            │
│              (Employee Model, Exceptions)               │
└─────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                   Infrastructure                        │
│    ┌─────────────────┐     ┌─────────────────┐         │
│    │CommandRepository│     │ QueryRepository │         │
│    │  (JdbcTemplate) │     │ (Spring Data)   │         │
│    └─────────────────┘     └─────────────────┘         │
│              └──── EmployeeEntity ────┘                 │
└─────────────────────────────────────────────────────────┘
```

### CQRS Repository 분리

- **CommandRepository**: 쓰기 전용 (JdbcTemplate bulk insert)
- **QueryRepository**: 읽기 전용 (Spring Data JPA)

### Parser 구조

```
EmployeeParserService (Facade)
    ├── CsvParser (implements EmployeeParser)
    └── JsonParser (implements EmployeeParser)
```

## 주요 기능

- 직원 연락처 등록/조회
- CSV/JSON 파일 업로드 및 파싱
- CSV/JSON 텍스트 직접 입력
- 페이징 처리
- 동명이인 검색 지원
- API Key 인증
- 입력값 검증 (이메일, 전화번호 형식)
- AOP 기반 로깅
- 전역 예외 처리
- OpenAPI 문서 자동 생성

## 테스트

- **Unit Tests**: Parser, Handler, Command 테스트
- **Integration Tests**: Controller 전체 API 테스트 (인증 포함)

```bash
# 전체 테스트 실행
./gradlew test

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

## 에러 응답

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid email: Invalid email format: invalid-email",
  "path": "/api/employee"
}
```

| 상태 코드 | 설명 |
|-----------|------|
| 400 | 잘못된 요청 (유효성 검증 실패, 잘못된 형식) |
| 401 | 인증 실패 (API Key 누락 또는 잘못됨) |
| 500 | 서버 내부 오류 |
