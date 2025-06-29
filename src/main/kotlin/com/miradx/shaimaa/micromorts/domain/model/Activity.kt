package com.miradx.shaimaa.micromorts.domain.model

data class Activity(
    val activity: String,
    val quantity: Float,
    val unit: ActionUnit,
    val miromorts: Float
) {
    companion object {
        fun create(
            activity: String,
            quantity: Float,
            unitValue: String,
            micromorts: Float
        ): Activity {
            val unit = ActionUnit.parseUnit(unitValue)

            return Activity(
                activity = activity,
                quantity = quantity,
                unit = unit,
                miromorts = micromorts
            )
        }
    }
}
