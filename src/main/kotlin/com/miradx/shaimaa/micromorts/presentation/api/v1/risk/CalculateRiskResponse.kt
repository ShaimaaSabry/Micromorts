package com.miradx.shaimaa.micromorts.presentation.api.v1.risk

import com.miradx.shaimaa.micromorts.application.commands.calculaterisk.CalculateRiskResult

data class CalculateRiskResponse(
    val commuterId: String,
    val risk: Float,
) {
    companion object {
        fun from(result: CalculateRiskResult): CalculateRiskResponse =
            CalculateRiskResponse(
                commuterId = result.commuterId,
                risk = result.risk,
            )
    }
}
