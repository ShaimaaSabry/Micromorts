package com.miradx.shaimaa.micromorts.domain.exception

open class BusinessRuleViolationException(
    override val message: String,
    val errors: Map<String, String> = emptyMap()
) : Exception(message)
