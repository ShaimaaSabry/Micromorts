package com.miradx.shaimaa.micromorts.presentation.api.v1.activity

import com.miradx.shaimaa.micromorts.application.commands.createactivities.CreateActivitiesCommand
import com.miradx.shaimaa.micromorts.domain.model.Activity
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero

data class ActivityDto(
    @field:NotBlank
    val activity: String?,
    @field:NotNull
    @field:Positive
    val quantity: Float?,
    @field:NotBlank
    val unit: String?,
    @field:NotNull
    @field:PositiveOrZero
    val micromorts: Float?,
) {
    fun toCommand(): CreateActivitiesCommand.Activity =
        CreateActivitiesCommand.Activity(
            activity = activity!!,
            quantity = quantity!!,
            unit = unit!!,
            micromorts = micromorts!!,
        )

    companion object {
        fun from(activity: Activity): ActivityDto =
            ActivityDto(
                activity = activity.activity,
                quantity = activity.quantity,
                unit = activity.unit.toString(),
                micromorts = activity.micromorts,
            )
    }
}
