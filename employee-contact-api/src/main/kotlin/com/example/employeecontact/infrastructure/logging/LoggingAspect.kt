package com.example.employeecontact.infrastructure.logging

import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Aspect
@Component
class LoggingAspect {

    @Around("execution(* com.example.employeecontact.presentation.controller.*.*(..))")
    fun logControllerMethods(joinPoint: ProceedingJoinPoint): Any? {
        val className = joinPoint.signature.declaringTypeName.substringAfterLast(".")
        val methodName = joinPoint.signature.name
        val args = joinPoint.args.map { it?.toString()?.take(100) ?: "null" }

        logger.info { ">> [$className.$methodName] Request received with args: $args" }

        val startTime = System.currentTimeMillis()

        return try {
            val result = joinPoint.proceed()
            val duration = System.currentTimeMillis() - startTime

            logger.info { "<< [$className.$methodName] Completed in ${duration}ms" }
            result
        } catch (ex: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error { "<< [$className.$methodName] Failed in ${duration}ms: ${ex.message}" }
            throw ex
        }
    }

    @Around("execution(* com.example.employeecontact.application.command.handler.*.*(..))")
    fun logCommandHandlers(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = joinPoint.signature.name
        logger.debug { ">> [Command] Executing: $methodName" }

        val startTime = System.currentTimeMillis()
        val result = joinPoint.proceed()
        val duration = System.currentTimeMillis() - startTime

        logger.debug { "<< [Command] $methodName completed in ${duration}ms" }
        return result
    }

    @Around("execution(* com.example.employeecontact.application.query.handler.*.*(..))")
    fun logQueryHandlers(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = joinPoint.signature.name
        logger.debug { ">> [Query] Executing: $methodName" }

        val startTime = System.currentTimeMillis()
        val result = joinPoint.proceed()
        val duration = System.currentTimeMillis() - startTime

        logger.debug { "<< [Query] $methodName completed in ${duration}ms" }
        return result
    }
}
