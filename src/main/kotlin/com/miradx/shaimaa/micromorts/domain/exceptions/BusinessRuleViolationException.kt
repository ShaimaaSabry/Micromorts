package com.miradx.shaimaa.micromorts.domain.exceptions

open class BusinessRuleViolationException(
    override val message: String,
    val errors: Map<String, String> = emptyMap(),
) : Exception(message)
