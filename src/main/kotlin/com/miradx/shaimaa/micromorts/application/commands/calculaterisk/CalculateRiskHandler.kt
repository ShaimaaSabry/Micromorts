package com.miradx.shaimaa.micromorts.application.commands.calculaterisk

import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import com.miradx.shaimaa.micromorts.domain.exceptions.CalculateRiskException
import com.miradx.shaimaa.micromorts.domain.model.Action
import org.slf4j.LoggerFactory

class CalculateRiskHandler(
    private val activityRepository: ActivityRepository,
) {
    operator fun invoke(command: CalculateRiskCommand): CalculateRiskResult {
        val actions =
            command.actions.map {
                Action.create(
                    it.timestamp,
                    it.action,
                    it.unit,
                    it.quantity,
                )
            }

        validateTimestamps(actions)

        val activities =
            this.activityRepository.searchActivities(
                actions.map { it.action },
            )
        logger.debug(
            "Activities found: {}",
            activities.entries.joinToString(prefix = "{", postfix = "}") { (action, opt) ->
                "$action=${opt.orElse(null)?.activity ?: "null"}"
            },
        )

        var sum = 0f
        for (action in actions) {
            val activity =
                activities[action.action]?.orElseThrow {
                    CalculateRiskException.activityNotFound(action.action)
                } ?: throw CalculateRiskException.activityNotFound(action.action)

            val risk = action.calculateRiskInMicromorts(activity)
            sum += risk
        }

        return CalculateRiskResult(
            commuterId = command.commuterId,
            risk = sum,
        )
    }

    private fun validateTimestamps(actions: List<Action>) {
        if (actions.isEmpty()) {
            return
        }

        val firstDate = actions.first().timestamp.toLocalDate()
        if (!actions.all { it.timestamp.toLocalDate() == firstDate }) {
            throw CalculateRiskException.inconsistentTimestamps()
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(CalculateRiskHandler::class.java)
    }
}
