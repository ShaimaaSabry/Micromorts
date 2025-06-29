package com.miradx.shaimaa.micromorts.application.commands.createactivities

import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class CreateActivitiesHandlerTest {
    private val mockActivityRepository = mockk<ActivityRepository>(relaxed = true)
    private val createActivitiesHandler = CreateActivitiesHandler(mockActivityRepository)

    @Test
    fun `given valid command, then should create activities successfully`() {
        // given
        val command =
            CreateActivitiesCommand(
                activities =
                    listOf(
                        CreateActivitiesCommand.Activity(
                            activity = "skiing",
                            quantity = 1f,
                            unit = "quantity",
                            micromorts = 0.7f,
                        ),
                        CreateActivitiesCommand.Activity(
                            activity = "swimming",
                            quantity = 2f,
                            unit = "quantity",
                            micromorts = 0.5f,
                        ),
                    ),
            )

        // when
        createActivitiesHandler(command)

        // then
        verify(exactly = 1) { mockActivityRepository.createActivities(any()) }
    }
}
