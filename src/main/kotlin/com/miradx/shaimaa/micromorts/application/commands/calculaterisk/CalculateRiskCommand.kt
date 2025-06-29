package com.miradx.shaimaa.micromorts.application.commands.calculaterisk

import java.time.LocalDateTime

data class CalculateRiskCommand(
    val commuterId: String,
    val actions: List<Action>,
) {
    data class Action(
        val timestamp: LocalDateTime,
        val action: String,
        val unit: String,
        val quantity: Float,
    )
}
