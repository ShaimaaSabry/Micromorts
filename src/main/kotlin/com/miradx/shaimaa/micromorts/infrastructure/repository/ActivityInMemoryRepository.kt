package com.miradx.shaimaa.micromorts.infrastructure.repository

import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import com.miradx.shaimaa.micromorts.domain.model.ActionUnit
import com.miradx.shaimaa.micromorts.domain.model.Activity
import org.springframework.stereotype.Component
import java.util.Optional

// @Primary
@Component
class ActivityInMemoryRepository(
    val _activities: MutableList<Activity> = mutableListOf(
        Activity(
            activity = "walked on sidewalk",
            quantity = 17f,
            unit = ActionUnit.MILE,
            miromorts = 1f
        ),
        Activity(
            activity = "rode a shark",
            quantity = 1f,
            unit = ActionUnit.MINUTE,
            miromorts = 1000f
        ),
        Activity(
            activity = "skiing",
            quantity = 1f,
            unit = ActionUnit.QUANTITY,
            miromorts = 0.7f
        ),
        Activity(
            activity = "skydiving",
            quantity = 1f,
            unit = ActionUnit.QUANTITY,
            miromorts = 8f
        ),
        Activity(
            activity = "base jumping",
            quantity = 1f,
            unit = ActionUnit.QUANTITY,
            miromorts = 430f
        ),
        Activity(
            activity = "mountaineering",
            quantity = 1f,
            unit = ActionUnit.QUANTITY,
            miromorts = 2840f
        )
    )
) : ActivityRepository {
    override fun getActivities(): List<Activity> {
        return _activities
    }

    override fun createActivities(activities: List<Activity>) {
        _activities.addAll(activities)
    }

    override fun searchActivities(action: String): Optional<Activity> {
        return _activities.firstOrNull { it.activity == action.lowercase() }
            ?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun searchActivities(actions: List<String>): Map<String, Optional<Activity>> {
        return actions.associateWith { action -> searchActivities(action) }
    }
}
