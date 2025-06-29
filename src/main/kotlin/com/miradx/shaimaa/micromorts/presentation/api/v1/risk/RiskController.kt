package com.miradx.shaimaa.micromorts.presentation.api.v1.risk

import com.miradx.shaimaa.micromorts.application.commands.calculaterisk.CalculateRiskHandler
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/risk")
@Tag(name = "Risk API", description = "Calculate the risk of death in micromorts from various activities performed by a person in a day.")
class RiskController(
    private val calculateRiskHandler: CalculateRiskHandler,
) {
    @PostMapping
    fun calculateRisk(
        @Valid @RequestBody
        request: CalculateRiskRequest,
    ): CalculateRiskResponse {
        val result = calculateRiskHandler(request.toCommand())

        return CalculateRiskResponse.from(
            result,
        )
    }
}
