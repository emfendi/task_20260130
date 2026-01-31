package com.example.employeecontact.infrastructure.entity

import com.example.employeecontact.domain.model.Employee
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "employees",
    indexes = [Index(name = "idx_employee_name", columnList = "name")]
)
class EmployeeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, unique = true, length = 255)
    val email: String,

    @Column(nullable = false, length = 20)
    val tel: String,

    @Column(nullable = false)
    val joined: LocalDate,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toModel(): Employee = Employee(
        id = requireNotNull(id) { "Entity must be persisted before converting to model" },
        name = name,
        email = email,
        tel = tel,
        joined = joined,
        createdAt = createdAt
    )
}
