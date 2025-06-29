package com.miradx.shaimaa.micromorts.domain.model

import com.miradx.shaimaa.micromorts.domain.exceptions.ActionException
import java.time.LocalDateTime

class Action(
    val timestamp: LocalDateTime,
    val action: String,
    val unit: ActionUnit,
    val quantity: Float,
) {
    companion object {
        fun create(
            timestamp: LocalDateTime,
            action: String,
            unitValue: String,
            quantity: Float,
        ): Action {
            val unit = ActionUnit.parse(unitValue)
            validateQuantity(quantity)

            return Action(
                timestamp = timestamp,
                action = action,
                unit = unit,
                quantity = quantity,
            )
        }

        private fun validateQuantity(quantity: Float) {
            if (quantity < 0) {
                throw ActionException.negativeQuantity(quantity)
            }
        }
    }

    fun calculateRiskInMicromorts(activity: Activity): Float {
        if (activity.unit != unit) {
            throw ActionException.invalidActionUnit(unit.name, activity.unit.name)
        }

        return activity.micromorts * quantity / activity.quantity
    }
}
