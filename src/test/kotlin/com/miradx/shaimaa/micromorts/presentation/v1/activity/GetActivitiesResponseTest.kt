package com.miradx.shaimaa.micromorts.presentation.v1.activity

import com.miradx.shaimaa.micromorts.domain.model.ActionUnit
import com.miradx.shaimaa.micromorts.domain.model.Activity
import com.miradx.shaimaa.micromorts.presentation.api.v1.activity.GetActivitiesResponse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetActivitiesResponseTest {
    @Nested
    inner class From {
        @Test
        fun `should convert activities to GetActivitiesResponse`() {
            // Given
            val activities =
                listOf(
                    Activity(
                        "Running",
                        1.0f,
                        unit = ActionUnit.MILE,
                        micromorts = 10f,
                    ),
                    Activity(
                        "Swimming",
                        2.0f,
                        unit = ActionUnit.MINUTE,
                        micromorts = 5f,
                    ),
                )

            // When
            val response = GetActivitiesResponse.from(activities)

            // Then
            assertEquals(activities.size, response.activities.size)
            assertEquals(activities[0].activity, response.activities[0].activity)
            assertEquals(activities[1].quantity, response.activities[1].quantity)
            assertEquals(activities[0].unit.name, response.activities[0].unit)
            assertEquals(activities[0].micromorts, response.activities[0].micromorts)
            assertEquals(activities[1].activity, response.activities[1].activity)
            assertEquals(activities[1].quantity, response.activities[1].quantity)
            assertEquals(activities[1].unit.name, response.activities[1].unit)
            assertEquals(activities[1].micromorts, response.activities[1].micromorts)
        }
    }
}
