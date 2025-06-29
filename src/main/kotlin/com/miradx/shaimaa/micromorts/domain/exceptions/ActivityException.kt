package com.miradx.shaimaa.micromorts.domain.exceptions

class ActivityException(
    message: String,
    errors: Map<String, String> = emptyMap(),
) : BusinessRuleViolationException(message, errors) {
    companion object {
        fun negativeQuantity(quantity: Float): ActivityException {
            val message = "Quantity can not be less than zero: $quantity"
            return ActivityException(
                message,
                mapOf("quantity" to message),
            )
        }

        fun negativeMicromorts(micromorts: Float): ActivityException {
            val message = "Micromorts can not be less than zero: $micromorts"
            return ActivityException(
                message,
                mapOf("micromorts" to message),
            )
        }
    }
}
