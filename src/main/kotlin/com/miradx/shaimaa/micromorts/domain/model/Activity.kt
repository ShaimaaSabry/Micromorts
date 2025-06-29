package com.miradx.shaimaa.micromorts.domain.model

import com.miradx.shaimaa.micromorts.domain.exceptions.ActivityException

data class Activity(
    val activity: String,
    val quantity: Float,
    val unit: ActionUnit,
    val micromorts: Float,
) {
    companion object {
        fun create(
            activity: String,
            quantity: Float,
            unitValue: String,
            micromorts: Float,
        ): Activity {
            val unit = ActionUnit.parse(unitValue)
            validateQuantity(quantity)
            validateMicromorts(micromorts)

            return Activity(
                activity = activity,
                quantity = quantity,
                unit = unit,
                micromorts = micromorts,
            )
        }

        private fun validateQuantity(quantity: Float) {
            if (quantity < 0) {
                throw ActivityException.negativeQuantity(quantity)
            }
        }

        private fun validateMicromorts(micromorts: Float) {
            if (micromorts < 0) {
                throw ActivityException.negativeMicromorts(micromorts)
            }
        }
    }
}
