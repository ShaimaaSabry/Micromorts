package com.miradx.shaimaa.micromorts.application.commands.calculaterisk

import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import com.miradx.shaimaa.micromorts.domain.exceptions.CalculateRiskException
import com.miradx.shaimaa.micromorts.domain.model.ActionUnit
import com.miradx.shaimaa.micromorts.domain.model.Activity
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals

class CalculateRiskHandlerTest {
    private val mockActivityRepository = mockk<ActivityRepository>()
    private val calculateRiskHandler = CalculateRiskHandler(mockActivityRepository)

    @Test
    fun `given a list of actions on the same day, when calculating risk, then should return the correct risk`() {
        // given
        val command =
            CalculateRiskCommand(
                commuterId = "COM-123",
                actions =
                    listOf(
                        CalculateRiskCommand.Action(
                            timestamp = LocalDateTime.now(),
                            action = "walked on sidewalk",
                            unit = "mile",
                            quantity = 51f,
                        ),
                        CalculateRiskCommand.Action(
                            timestamp = LocalDateTime.now(),
                            action = "skydiving",
                            unit = "quantity",
                            quantity = 2f,
                        ),
                        CalculateRiskCommand.Action(
                            timestamp = LocalDateTime.now(),
                            action = "base jumping",
                            unit = "quantity",
                            quantity = 1f,
                        ),
                    ),
            )

        val activities =
            mapOf(
                "walked on sidewalk" to
                    Optional.of(
                        Activity(
                            activity = "walked on sidewalk",
                            quantity = 17f,
                            unit = ActionUnit.MILE,
                            micromorts = 1f,
                        ),
                    ),
                "skydiving" to
                    Optional.of(
                        Activity(
                            activity = "skydiving",
                            quantity = 1f,
                            unit = ActionUnit.QUANTITY,
                            micromorts = 8f,
                        ),
                    ),
                "base jumping" to
                    Optional.of(
                        Activity(
                            activity = "base jumping",
                            quantity = 1f,
                            unit = ActionUnit.QUANTITY,
                            micromorts = 430f,
                        ),
                    ),
            )

        every {
            mockActivityRepository.searchActivities(
                command.actions.map { it.action },
            )
        } returns activities

        // when
        val result = calculateRiskHandler(command)

        // then
        assertEquals(command.commuterId, result.commuterId)
        assertEquals(449f, result.risk)
    }

    @Test
    fun `given an empty list of actions, when calculating risk, then should return 0`() {
        // given
        val command =
            CalculateRiskCommand(
                commuterId = "COM-123",
                actions = emptyList(),
            )

        every {
            mockActivityRepository.searchActivities(
                command.actions.map { it.action },
            )
        } returns emptyMap()

        // when
        val result = calculateRiskHandler(command)

        // then
        assertEquals(command.commuterId, result.commuterId)
        assertEquals(0f, result.risk)
    }

    @Test
    fun `given a list of actions on different days, when calculating risk, then should throw an exception`() {
        // given
        val command =
            CalculateRiskCommand(
                commuterId = "COM-123",
                actions =
                    listOf(
                        CalculateRiskCommand.Action(
                            timestamp = LocalDateTime.now(),
                            action = "walked on sidewalk",
                            unit = "mile",
                            quantity = 51f,
                        ),
                        CalculateRiskCommand.Action(
                            timestamp = LocalDateTime.now().plusDays(1),
                            action = "skydiving",
                            unit = "quantity",
                            quantity = 2f,
                        ),
                    ),
            )

        // when & then
        val ex = assertThrows<CalculateRiskException> { calculateRiskHandler(command) }

        // then
        assertEquals("Timestamps must be in the same day", ex.message)
    }

    @Test
    fun `given an action with a non-existent activity (empty optional), when calculating risk, then should throw an exception`() {
        // given
        val command =
            CalculateRiskCommand(
                commuterId = "COM-123",
                actions =
                    listOf(
                        CalculateRiskCommand.Action(
                            timestamp = LocalDateTime.now(),
                            action = "skiing",
                            unit = "quantity",
                            quantity = 31f,
                        ),
                        CalculateRiskCommand.Action(
                            timestamp = LocalDateTime.now(),
                            action = "non-existent activity",
                            unit = "quantity",
                            quantity = 1f,
                        ),
                    ),
            )

        every {
            mockActivityRepository.searchActivities(
                command.actions.map { it.action },
            )
        } returns
            mapOf(
                "skiing" to
                    Optional.of(
                        Activity(
                            activity = "skiing",
                            quantity = 1f,
                            unit = ActionUnit.QUANTITY,
                            micromorts = 0.7f,
                        ),
                    ),
                "non-existent activity" to Optional.empty(),
            )

        // when & then
        val ex = assertThrows<CalculateRiskException> { calculateRiskHandler(command) }

        // then
        assertEquals("Activity not found for action: non-existent activity", ex.message)
    }

    @Test
    fun `given an action with a non-existent activity (null), when calculating risk, then should throw an exception`() {
        // given
        val command =
            CalculateRiskCommand(
                commuterId = "COM-123",
                actions =
                    listOf(
                        CalculateRiskCommand.Action(
                            timestamp = LocalDateTime.now(),
                            action = "skiing",
                            unit = "quantity",
                            quantity = 31f,
                        ),
                        CalculateRiskCommand.Action(
                            timestamp = LocalDateTime.now(),
                            action = "non-existent activity",
                            unit = "quantity",
                            quantity = 1f,
                        ),
                    ),
            )

        every {
            mockActivityRepository.searchActivities(
                command.actions.map { it.action },
            )
        } returns
            mapOf(
                "skiing" to
                    Optional.of(
                        Activity(
                            activity = "skiing",
                            quantity = 1f,
                            unit = ActionUnit.QUANTITY,
                            micromorts = 0.7f,
                        ),
                    ),
            )

        // when & then
        val ex = assertThrows<CalculateRiskException> { calculateRiskHandler(command) }

        // then
        assertEquals("Activity not found for action: non-existent activity", ex.message)
    }
}
