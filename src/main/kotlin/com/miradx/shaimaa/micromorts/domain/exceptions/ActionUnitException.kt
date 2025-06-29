package com.miradx.shaimaa.micromorts.domain.exceptions

class ActionUnitException(
    message: String,
    errors: Map<String, String> = emptyMap(),
) : BusinessRuleViolationException(message, errors) {
    companion object {
        fun invalidUnit(value: String): ActionUnitException {
            val message = "Invalid unrecognized unit: $value"
            return ActionUnitException(
                message,
                mapOf("unit" to message),
            )
        }
    }
}
