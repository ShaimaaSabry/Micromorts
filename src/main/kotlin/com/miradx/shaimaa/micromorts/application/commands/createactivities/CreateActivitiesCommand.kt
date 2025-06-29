package com.miradx.shaimaa.micromorts.application.commands.createactivities

data class CreateActivitiesCommand(
    val activities: List<Activity>,
) {
    data class Activity(
        val activity: String,
        val quantity: Float,
        val unit: String,
        val micromorts: Float,
    )
}
