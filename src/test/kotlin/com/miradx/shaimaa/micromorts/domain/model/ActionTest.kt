package com.miradx.shaimaa.micromorts.domain.model

import com.miradx.shaimaa.micromorts.domain.exceptions.ActionException
import com.miradx.shaimaa.micromorts.domain.exceptions.ActionUnitException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ActionTest {
    @Nested
    inner class Create {
        @Test
        fun `should create action given valid parameters`() {
            // when
            val timestamp = LocalDateTime.now()
            val action =
                Action.create(
                    timestamp = timestamp,
                    action = "Running",
                    quantity = 5f,
                    unitValue = "minute",
                )

            // then
            assertEquals(timestamp, action.timestamp)
            assertEquals("Running", action.action)
            assertEquals(5f, action.quantity)
            assertEquals(ActionUnit.MINUTE, action.unit)
        }

        @Test
        fun `should throw exception given negative quantity`() {
            // when & then
            val exception =
                assertThrows<ActionException> {
                    Action.create(
                        timestamp = LocalDateTime.now(),
                        action = "Running",
                        quantity = -5f,
                        unitValue = "minute",
                    )
                }

            // then
            assertEquals("Quantity can not be less than zero: -5.0", exception.message)
        }

        @Test
        fun `should throw exception given invalid unit`() {
            // when & then
            val exception =
                assertThrows<ActionUnitException> {
                    Action.create(
                        timestamp = LocalDateTime.now(),
                        action = "Running",
                        quantity = 5f,
                        unitValue = "invalid_unit",
                    )
                }

            // then
            assertEquals("Invalid unrecognized unit: invalid_unit", exception.message)
        }
    }

    @Nested
    inner class CalculateRiskInMicromorts {
        @Test
        fun `should calculate risk in micromorts given matching units`() {
            // given
            val action =
                Action.create(
                    timestamp = LocalDateTime.now(),
                    action = "Running",
                    quantity = 5f,
                    unitValue = "minute",
                )
            val activity =
                Activity.create(
                    activity = "Running",
                    quantity = 10f,
                    unitValue = "minute",
                    micromorts = 20f,
                )

            // when
            val risk = action.calculateRiskInMicromorts(activity)

            // then
            assertEquals(10f, risk)
        }

        @Test
        fun `should throw exception given mismatched units`() {
            // given
            val action =
                Action.create(
                    timestamp = LocalDateTime.now(),
                    action = "Running",
                    quantity = 5f,
                    unitValue = "minute",
                )
            val activity =
                Activity.create(
                    activity = "Running",
                    quantity = 10f,
                    unitValue = "mile",
                    micromorts = 20f,
                )

            // when & then
            val exception =
                assertThrows<ActionException> {
                    action.calculateRiskInMicromorts(activity)
                }
            assertEquals("Action unit MINUTE does not match the expected unit MILE", exception.message)
        }
    }
}
