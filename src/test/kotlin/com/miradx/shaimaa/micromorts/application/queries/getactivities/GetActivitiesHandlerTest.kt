package com.miradx.shaimaa.micromorts.application.queries.getactivities

import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import com.miradx.shaimaa.micromorts.domain.model.ActionUnit
import com.miradx.shaimaa.micromorts.domain.model.Activity
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetActivitiesHandlerTest {
    private val mockActivityRepository = mockk<ActivityRepository>()
    private val getActivitiesHandler = GetActivitiesHandler(mockActivityRepository)

    @Test
    fun `given a request to get activities, then should return the list of activities`() {
        // given
        val activities =
            listOf(
                Activity(activity = "walking", quantity = 1f, unit = ActionUnit.MILE, micromorts = 0.1f),
                Activity(activity = "running", quantity = 2f, unit = ActionUnit.MINUTE, micromorts = 0.2f),
            )

        every { mockActivityRepository.getActivities() } returns activities

        // when
        val result = getActivitiesHandler()

        // then
        assertEquals(activities, result)
    }
}
