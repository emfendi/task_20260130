package com.example.employeecontact.infrastructure.persistence

import com.example.employeecontact.infrastructure.entity.EmployeeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeQueryRepository : JpaRepository<EmployeeEntity, Long> {
    fun findByName(name: String): List<EmployeeEntity>
}
