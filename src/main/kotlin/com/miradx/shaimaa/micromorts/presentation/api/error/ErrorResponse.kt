package com.miradx.shaimaa.micromorts.presentation.api.error

data class ErrorResponse(
    val errorMessage: String,
    val errors: Map<String, String> = emptyMap(),
)
