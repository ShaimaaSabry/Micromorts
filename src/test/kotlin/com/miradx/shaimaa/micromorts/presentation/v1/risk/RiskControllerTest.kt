package com.miradx.shaimaa.micromorts.presentation.v1.risk

import com.fasterxml.jackson.databind.ObjectMapper
import com.miradx.shaimaa.micromorts.application.commands.calculaterisk.CalculateRiskHandler
import com.miradx.shaimaa.micromorts.application.commands.calculaterisk.CalculateRiskResult
import com.miradx.shaimaa.micromorts.domain.exceptions.CalculateRiskException
import com.miradx.shaimaa.micromorts.presentation.api.v1.risk.ActionDto
import com.miradx.shaimaa.micromorts.presentation.api.v1.risk.CalculateRiskRequest
import com.miradx.shaimaa.micromorts.presentation.api.v1.risk.RiskController
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import java.util.stream.Stream

@WebMvcTest(RiskController::class)
@Import(RiskControllerTest.MockedBeans::class)
class RiskControllerTest {
    @TestConfiguration
    class MockedBeans {
        @Bean
        fun calculateRiskHandler(): CalculateRiskHandler = mockk(relaxed = true)
    }

    @Autowired
    private lateinit var mockCalculateRiskHandler: CalculateRiskHandler

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class CalculateRisk {
        @Test
        fun `given valid request, then returns 200 OK and micromort risk from handler`() {
            // given
            val result =
                CalculateRiskResult(
                    commuterId = "COM-123",
                    risk = 25f,
                )

            every { mockCalculateRiskHandler(any()) } returns result

            val request =
                mapOf(
                    "commuterId" to "COM-123",
                    "actions" to
                        listOf(
                            mapOf(
                                "timestamp" to "2025-07-16 07:40:55",
                                "action" to "walking",
                                "unit" to "minutes",
                                "quantity" to 30f,
                            ),
                            mapOf(
                                "timestamp" to "2025-07-16 08:15:20",
                                "action" to "cycling",
                                "unit" to "minutes",
                                "quantity" to 20f,
                            ),
                        ),
                )

            // when, then
            mockMvc
                .post("/v1/risk") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andDo {
                    print()
                }.andExpect {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$.commuterId") { value(result.commuterId) }
                        jsonPath("$.risk") { value(result.risk) }
                    }
                }
        }

        @Test
        fun `given a business rule violation exception, then returns 400 Bad Request`() {
            // given
            every { mockCalculateRiskHandler(any()) } throws CalculateRiskException.inconsistentTimestamps()

            val request =
                CalculateRiskRequest(
                    commuterId = "COM-123",
                    actions =
                        listOf(
                            ActionDto(
                                timestamp = LocalDateTime.now(),
                                action = "walking",
                                unit = "minutes",
                                quantity = 30f,
                            ),
                            ActionDto(
                                timestamp = LocalDateTime.now(),
                                action = "cycling",
                                unit = "minutes",
                                quantity = 20f,
                            ),
                        ),
                )

            // when, then
            mockMvc
                .post("/v1/risk") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$.errorMessage") { value("Timestamps must be in the same day") }
                    }
                }
        }

        @ParameterizedTest
        @MethodSource("invalidRequestsProvider")
        fun `given invalid request, then returns 400 Bad Request`(
            request: CalculateRiskRequest,
            expectedErrorMessage: String,
            expectedErrors: Map<String, String>,
        ) {
            // when, then
            mockMvc
                .post("/v1/risk") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$.errorMessage") { value(expectedErrorMessage) }
                        jsonPath("$.errors") { value(expectedErrors) }
                    }
                }
        }

        private fun invalidRequestsProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    CalculateRiskRequest(
                        commuterId = null,
                        actions = null,
                    ),
                    "Request validation errors",
                    mapOf(
                        "commuterId" to "must not be blank",
                        "actions" to "must not be null",
                    ),
                ),
                Arguments.of(
                    CalculateRiskRequest(
                        commuterId = "",
                        actions = emptyList(),
                    ),
                    "Request validation errors",
                    mapOf(
                        "commuterId" to "must not be blank",
                        "actions" to "size must be between 1 and 2147483647",
                    ),
                ),
                Arguments.of(
                    CalculateRiskRequest(
                        commuterId = "COM-123",
                        actions =
                            listOf(
                                ActionDto(
                                    timestamp = null,
                                    action = null,
                                    unit = null,
                                    quantity = null,
                                ),
                            ),
                    ),
                    "Request validation errors",
                    mapOf(
                        "actions[0].timestamp" to "must not be null",
                        "actions[0].action" to "must not be blank",
                        "actions[0].unit" to "must not be blank",
                        "actions[0].quantity" to "must not be null",
                    ),
                ),
                Arguments.of(
                    CalculateRiskRequest(
                        commuterId = "COM-123",
                        actions =
                            listOf(
                                ActionDto(
                                    timestamp = LocalDateTime.now(),
                                    action = "",
                                    unit = "",
                                    quantity = -1f,
                                ),
                            ),
                    ),
                    "Request validation errors",
                    mapOf(
                        "actions[0].action" to "must not be blank",
                        "actions[0].unit" to "must not be blank",
                        "actions[0].quantity" to "must be greater than or equal to 0",
                    ),
                ),
            )
    }
}
