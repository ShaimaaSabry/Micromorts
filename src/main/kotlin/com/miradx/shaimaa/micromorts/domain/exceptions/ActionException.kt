package com.miradx.shaimaa.micromorts.domain.exceptions

class ActionException(
    message: String,
    errors: Map<String, String> = emptyMap(),
) : BusinessRuleViolationException(message, errors) {
    companion object {
        fun negativeQuantity(quantity: Float): ActionException {
            val message = "Quantity can not be less than zero: $quantity"
            return ActionException(
                message,
                mapOf("quantity" to message),
            )
        }

        fun invalidActionUnit(
            actionUnit: String,
            activityUnit: String,
        ): ActionException {
            val message = "Action unit $actionUnit does not match the expected unit $activityUnit"
            return ActionException(
                message,
                mapOf("unit" to message),
            )
        }
    }
}
