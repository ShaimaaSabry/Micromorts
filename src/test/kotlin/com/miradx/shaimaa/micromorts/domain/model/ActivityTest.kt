package com.miradx.shaimaa.micromorts.domain.model

import com.miradx.shaimaa.micromorts.domain.exceptions.ActionUnitException
import com.miradx.shaimaa.micromorts.domain.exceptions.ActivityException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ActivityTest {
    @Nested
    inner class Create {
        @Test
        fun `should create activity given valid parameters`() {
            // when
            val activity =
                Activity.create(
                    activity = "Running",
                    quantity = 5f,
                    unitValue = "minute",
                    micromorts = 10f,
                )

            // then
            assertEquals("Running", activity.activity)
            assertEquals(5f, activity.quantity)
            assertEquals(ActionUnit.MINUTE, activity.unit)
            assertEquals(10f, activity.micromorts)
        }

        @Test
        fun `should throw exception given negative quantity`() {
            // when & then
            val exception =
                assertThrows(ActivityException::class.java) {
                    Activity.create(
                        activity = "Running",
                        quantity = -5f,
                        unitValue = "minute",
                        micromorts = 10f,
                    )
                }

            // then
            assertEquals("Quantity can not be less than zero: -5.0", exception.message)
        }

        @Test
        fun `should throw exception given negative micromorts`() {
            // when & then
            val exception =
                assertThrows(ActivityException::class.java) {
                    Activity.create(
                        activity = "Running",
                        quantity = 5f,
                        unitValue = "minute",
                        micromorts = -10f,
                    )
                }

            // then
            assertEquals("Micromorts can not be less than zero: -10.0", exception.message)
        }

        @Test
        fun `should throw exception given invalid unit`() {
            // when & then
            val exception =
                assertThrows(ActionUnitException::class.java) {
                    Activity.create(
                        activity = "Running",
                        quantity = 5f,
                        unitValue = "invalid_unit",
                        micromorts = 10f,
                    )
                }

            // then
            assertEquals("Invalid unrecognized unit: invalid_unit", exception.message)
        }
    }
}
