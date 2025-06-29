package com.miradx.shaimaa.micromorts.presentation.v1.risk

import com.miradx.shaimaa.micromorts.application.commands.calculaterisk.CalculateRiskResult
import com.miradx.shaimaa.micromorts.presentation.api.v1.risk.CalculateRiskResponse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CalculateRiskResponseTest {
    @Nested
    inner class From {
        @Test
        fun `should convert CalculateRiskResult to CalculateRiskResponse`() {
            // given
            val result = CalculateRiskResult(commuterId = "123", risk = 0.5f)

            // when
            val response = CalculateRiskResponse.from(result)

            // then
            assertEquals(result.commuterId, response.commuterId)
            assertEquals(result.risk, response.risk)
        }
    }
}
