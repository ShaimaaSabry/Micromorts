package com.miradx.shaimaa.micromorts.presentation.v1.activity

import com.miradx.shaimaa.micromorts.presentation.api.v1.activity.ActivityDto
import com.miradx.shaimaa.micromorts.presentation.api.v1.activity.CreateActivitiesRequest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateActivitiesRequestTest {
    @Nested
    inner class ToCommand {
        @Test
        fun `should convert to CreateActivitiesCommand`() {
            // Given
            val request =
                CreateActivitiesRequest(
                    activities =
                        listOf(
                            ActivityDto(
                                activity = "skiing",
                                quantity = 1f,
                                unit = "quantity",
                                micromorts = 3f,
                            ),
                            ActivityDto(
                                activity = "swimming",
                                quantity = 2f,
                                unit = "minute",
                                micromorts = 5f,
                            ),
                        ),
                )

            // When
            val command = request.toCommand()

            // Then
            assertEquals(request.activities!!.size, command.activities.size)
            assertEquals(request.activities!![0].activity, command.activities[0].activity)
            assertEquals(request.activities!![0].quantity, command.activities[0].quantity)
            assertEquals(request.activities!![0].unit, command.activities[0].unit)
            assertEquals(request.activities!![0].micromorts, command.activities[0].micromorts)
        }
    }
}
