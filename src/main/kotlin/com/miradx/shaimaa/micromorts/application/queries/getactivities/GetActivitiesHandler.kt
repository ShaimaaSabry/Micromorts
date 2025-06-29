package com.miradx.shaimaa.micromorts.application.queries.getactivities

import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import com.miradx.shaimaa.micromorts.domain.model.Activity

class GetActivitiesHandler(
    private val activityRepository: ActivityRepository,
) {
    operator fun invoke(): List<Activity> = activityRepository.getActivities()
}
