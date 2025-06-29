package com.miradx.shaimaa.micromorts.api.util

import io.restassured.RestAssured
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import org.junit.jupiter.api.BeforeAll

open class BaseIT {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            RestAssured.baseURI = Config.targetUrl()
            RestAssured.filters(
                RequestLoggingFilter(),
                ResponseLoggingFilter(),
            )
        }
    }
}
