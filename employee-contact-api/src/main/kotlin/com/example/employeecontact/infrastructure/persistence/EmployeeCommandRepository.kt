package com.example.employeecontact.infrastructure.persistence

import com.example.employeecontact.infrastructure.entity.EmployeeEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.LocalDateTime

interface EmployeeCommandRepository {
    fun saveAll(entities: List<EmployeeEntity>): Int
}

@Repository
class JpaEmployeeCommandRepository(
    private val jdbcTemplate: JdbcTemplate
) : EmployeeCommandRepository {

    override fun saveAll(entities: List<EmployeeEntity>): Int {
        if (entities.isEmpty()) return 0

        val now = LocalDateTime.now()
        val sql = """
            INSERT INTO employees (name, email, tel, joined, created_at)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, entities, entities.size) { ps, entity ->
            ps.setString(1, entity.name)
            ps.setString(2, entity.email)
            ps.setString(3, entity.tel)
            ps.setObject(4, entity.joined)
            ps.setTimestamp(5, Timestamp.valueOf(now))
        }

        return entities.size
    }
}
