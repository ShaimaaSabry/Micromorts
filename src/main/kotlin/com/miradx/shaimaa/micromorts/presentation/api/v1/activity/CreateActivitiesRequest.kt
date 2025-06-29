package com.miradx.shaimaa.micromorts.presentation.api.v1.activity

import com.miradx.shaimaa.micromorts.application.commands.createactivities.CreateActivitiesCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateActivitiesRequest(
    @field:NotNull
    @field:Size(min = 1)
    @field:Valid
    val activities: List<ActivityDto>?,
) {
    fun toCommand(): CreateActivitiesCommand =
        CreateActivitiesCommand(
            activities = activities!!.stream().map { it.toCommand() }.toList(),
        )
}
