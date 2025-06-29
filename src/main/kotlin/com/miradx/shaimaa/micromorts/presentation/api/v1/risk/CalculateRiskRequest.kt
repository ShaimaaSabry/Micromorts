package com.miradx.shaimaa.micromorts.presentation.api.v1.risk

import com.miradx.shaimaa.micromorts.application.commands.calculaterisk.CalculateRiskCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CalculateRiskRequest(
    @field:NotBlank
    val commuterId: String?,
    @field:NotNull
    @field:Size(min = 1)
    @field:Valid
    val actions: List<ActionDto>?,
) {
    fun toCommand(): CalculateRiskCommand =
        CalculateRiskCommand(
            commuterId = commuterId!!,
            actions = actions!!.stream().map { it.toCommand() }.toList(),
        )
}
