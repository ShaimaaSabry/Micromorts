package com.miradx.shaimaa.micromorts.application.contracts

import com.miradx.shaimaa.micromorts.domain.model.Activity
import java.util.Optional

interface ActivityRepository {
    fun getActivities(): List<Activity>

    fun createActivities(activities: List<Activity>)

    fun searchActivities(action: String): Optional<Activity>

    fun searchActivities(actions: List<String>): Map<String, Optional<Activity>>
}
