package com.miradx.shaimaa.micromorts.application.commands.createactivities

import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import com.miradx.shaimaa.micromorts.domain.model.Activity

class CreateActivitiesHandler(
    private val activityRepository: ActivityRepository,
) {
    operator fun invoke(command: CreateActivitiesCommand) {
        val activities =
            command.activities
                .map {
                    Activity.create(
                        activity = it.activity,
                        quantity = it.quantity,
                        unitValue = it.unit,
                        micromorts = it.micromorts,
                    )
                }.toList()

        activityRepository.createActivities(activities)
    }
}
