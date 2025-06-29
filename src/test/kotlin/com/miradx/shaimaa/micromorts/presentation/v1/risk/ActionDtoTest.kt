package com.miradx.shaimaa.micromorts.presentation.v1.risk

import com.miradx.shaimaa.micromorts.presentation.api.v1.risk.ActionDto
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ActionDtoTest {
    @Nested
    inner class ToCommand {
        @Test
        fun `should convert ActionDto to CalculateRiskCommand-Action`() {
            // Given
            val dto =
                ActionDto(
                    timestamp = LocalDateTime.now(),
                    action = "Running",
                    unit = "mile",
                    quantity = 5f,
                )

            // When
            val command = dto.toCommand()

            // Then
            assertEquals(dto.timestamp, command.timestamp)
            assertEquals(dto.action, command.action)
            assertEquals(dto.unit, command.unit)
            assertEquals(dto.quantity, command.quantity)
        }
    }
}
