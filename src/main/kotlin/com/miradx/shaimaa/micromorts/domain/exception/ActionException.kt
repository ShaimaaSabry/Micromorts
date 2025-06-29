package com.miradx.shaimaa.micromorts.domain.exception

class ActionException(
    message: String,
    errors: Map<String, String> = emptyMap()
) : BusinessRuleViolationException(message, errors) {
    companion object {
        fun invalidActionType(value: String): ActionException {
            val message = "Invalid action: $value"
            return ActionException(
                message,
                mapOf("action" to message)
            )
        }

        fun invalidActionUnit(value: String): ActionException {
            val message = "Invalid unit: $value"
            return ActionException(
                message,
                mapOf("unit" to message)
            )
        }

        fun negativeQuantity(quantity: Float): ActionException {
            val message = "Quantity can not be less than zero: $quantity"
            return ActionException(
                message,
                mapOf("quantity" to message)
            )
        }
    }
}
