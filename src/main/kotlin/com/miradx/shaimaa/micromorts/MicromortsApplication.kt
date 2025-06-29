package com.miradx.shaimaa.micromorts

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
    info =
        Info(
            title = "Micromorts API",
            version = "1.0.0",
            description = "APIs that calculate the risk of death in micromorts.",
        ),
)
class MicromortsApplication

fun main(args: Array<String>) {
    runApplication<MicromortsApplication>(*args)
}
