package com.miradx.shaimaa.micromorts.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.miradx.shaimaa.micromorts.api.util.BaseIT
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.Test

class CalculateRiskIT : BaseIT() {
    private val mapper = jacksonObjectMapper()

    @Test
    fun `should return risk value for given actions`() {
        val requestBody =
            mapOf(
                "commuterId" to "commuterId",
                "actions" to
                    listOf(
                        mapOf(
                            "timestamp" to "2022-01-01 10:30:09",
                            "action" to "walked on sidewalk",
                            "unit" to "mile",
                            "quantity" to 1,
                        ),
                        mapOf(
                            "timestamp" to "2022-01-01 10:30:09",
                            "action" to "rode a shark",
                            "unit" to "minute",
                            "quantity" to 3,
                        ),
                    ),
            )

        val jsonPayload = mapper.writeValueAsString(requestBody)

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(jsonPayload)
            .`when`()
            .post("/v1/risk")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("commuterId", equalTo("commuterId"))
            .body("risk", greaterThan(0f))
    }
}
