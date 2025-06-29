package com.miradx.shaimaa.micromorts.domain.exceptions

class CalculateRiskException(
    message: String,
    errors: Map<String, String> = emptyMap(),
) : BusinessRuleViolationException(message, errors) {
    companion object {
        fun inconsistentTimestamps(): CalculateRiskException {
            val message = "Timestamps must be in the same day"
            return CalculateRiskException(
                message,
                mapOf("timestamp" to message),
            )
        }

        fun activityNotFound(action: String): CalculateRiskException {
            val message = "Activity not found for action: $action"
            return CalculateRiskException(
                message,
                mapOf("action" to message),
            )
        }
    }
}
