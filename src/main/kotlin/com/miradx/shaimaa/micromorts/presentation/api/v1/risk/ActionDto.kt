package com.miradx.shaimaa.micromorts.presentation.api.v1.risk

import com.fasterxml.jackson.annotation.JsonFormat
import com.miradx.shaimaa.micromorts.application.commands.calculaterisk.CalculateRiskCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime

data class ActionDto(
    @field:NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime?,
    @field:NotBlank
    val action: String?,
    @field:NotBlank
    val unit: String?,
    @field:NotNull
    @field:PositiveOrZero
    val quantity: Float?,
) {
    fun toCommand(): CalculateRiskCommand.Action =
        CalculateRiskCommand.Action(
            timestamp = timestamp!!,
            action = action!!,
            unit = unit!!,
            quantity = quantity!!,
        )
}
