package com.miradx.shaimaa.micromorts.presentation.v1.activity

import com.miradx.shaimaa.micromorts.domain.model.ActionUnit
import com.miradx.shaimaa.micromorts.domain.model.Activity
import com.miradx.shaimaa.micromorts.presentation.api.v1.activity.ActivityDto
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ActivityDtoTest {
    @Nested
    inner class ToCommand {
        @Test
        fun `should convert ActivityDto to CreateActivitiesCommand-Activity`() {
            // Given
            val dto =
                ActivityDto(
                    activity = "Running",
                    quantity = 1.0f,
                    unit = "hour",
                    micromorts = 10.0f,
                )

            // When
            val command = dto.toCommand()

            // Then
            assertEquals(dto.activity, command.activity)
            assertEquals(dto.quantity, command.quantity)
            assertEquals(dto.unit, command.unit)
            assertEquals(dto.micromorts, command.micromorts)
        }
    }

    @Nested
    inner class From {
        @Test
        fun `should convert Activity to ActivityDto`() {
            // Given
            val activity =
                Activity(
                    activity = "Swimming",
                    quantity = 2.0f,
                    unit = ActionUnit.MINUTE,
                    micromorts = 5.0f,
                )

            // When
            val dto = ActivityDto.from(activity)

            // Then
            assertEquals(activity.activity, dto.activity)
            assertEquals(activity.quantity, dto.quantity)
            assertEquals(activity.unit.toString(), dto.unit)
            assertEquals(activity.micromorts, dto.micromorts)
        }
    }
}
