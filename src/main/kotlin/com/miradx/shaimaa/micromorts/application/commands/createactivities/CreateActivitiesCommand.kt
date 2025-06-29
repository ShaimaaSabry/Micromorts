package com.miradx.shaimaa.micromorts.application.commands.createactivities

import com.miradx.shaimaa.micromorts.domain.model.Activity

data class CreateActivitiesCommand(
    val activities: List<Activity>
) {
    data class Activity(
        val activity: String,
        val quantity: Float,
        val unit: String,
        val miromorts: Float
    )
}
