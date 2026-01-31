package com.example.employeecontact.application.command.handler

import com.example.employeecontact.application.command.dto.CreateEmployeeCommand
import com.example.employeecontact.infrastructure.entity.EmployeeEntity
import com.example.employeecontact.infrastructure.persistence.EmployeeCommandRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class CreateEmployeeCommandHandler(
    private val commandRepository: EmployeeCommandRepository
) {

    @Transactional
    fun handleBatch(commands: List<CreateEmployeeCommand>): Int {
        logger.debug { "Creating ${commands.size} employees in batch" }

        val entities = commands.map { createEntity(it) }

        return commandRepository.saveAll(entities).also {
            logger.info { "Batch creation completed: $it employees created" }
        }
    }

    private fun createEntity(command: CreateEmployeeCommand): EmployeeEntity {
        return EmployeeEntity(
            name = command.name,
            email = command.email,
            tel = normalizePhoneNumber(command.tel),
            joined = command.joined
        )
    }

    private fun normalizePhoneNumber(tel: String): String {
        return tel.replace("-", "").replace(" ", "")
    }
}
