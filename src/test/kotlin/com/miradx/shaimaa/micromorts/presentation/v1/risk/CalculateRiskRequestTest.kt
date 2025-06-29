package com.miradx.shaimaa.micromorts.presentation.v1.risk

import com.miradx.shaimaa.micromorts.presentation.api.v1.risk.ActionDto
import com.miradx.shaimaa.micromorts.presentation.api.v1.risk.CalculateRiskRequest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class CalculateRiskRequestTest {
    @Nested
    inner class ToCommand {
        @Test
        fun `should convert to CalculateRiskCommand`() {
            // given
            val request =
                CalculateRiskRequest(
                    commuterId = "commuter123",
                    actions =
                        listOf(
                            ActionDto(
                                timestamp = LocalDateTime.now(),
                                action = "running",
                                unit = "mile",
                                quantity = 3f,
                            ),
                        ),
                )

            // when
            val command = request.toCommand()

            // then
            assertEquals(request.commuterId, command.commuterId)
            assertEquals(request.actions!!.size, command.actions.size)
            assertEquals(request.actions!![0].timestamp, command.actions[0].timestamp)
            assertEquals(request.actions!![0].action, command.actions[0].action)
            assertEquals(request.actions!![0].unit, command.actions[0].unit)
            assertEquals(request.actions!![0].quantity, command.actions[0].quantity)
        }
    }
}
