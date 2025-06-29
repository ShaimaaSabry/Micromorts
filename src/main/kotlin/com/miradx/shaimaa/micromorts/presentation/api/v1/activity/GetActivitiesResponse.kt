package com.miradx.shaimaa.micromorts.presentation.api.v1.activity

import com.miradx.shaimaa.micromorts.domain.model.Activity

data class GetActivitiesResponse(
    val activities: List<ActivityDto>,
) {
    companion object {
        fun from(activities: List<Activity>): GetActivitiesResponse =
            GetActivitiesResponse(
                activities =
                    activities
                        .stream()
                        .map { activity ->
                            ActivityDto.from(activity)
                        }.toList(),
            )
    }
}
