package com.miradx.shaimaa.micromorts.presentation.api.error

import com.miradx.shaimaa.micromorts.domain.exceptions.BusinessRuleViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.function.client.WebClientResponseException

@ControllerAdvice
class GlobalControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handle400ValidationException(ex: MethodArgumentNotValidException): ErrorResponse {
        val errors =
            ex.bindingResult.fieldErrors.associate {
                it.field to (it.defaultMessage ?: "Invalid value")
            }

        val errorMessage =
            if (errors.size == 1) {
                val (key, value) = errors.entries.first()
                "$key $value"
            } else {
                "Request validation errors"
            }

        val errorResponse =
            ErrorResponse(
                errorMessage = errorMessage,
                errors = errors,
            )

        logger.error("validation exception: {}", errorResponse)
        return errorResponse
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handle400JsonParsingException(ex: HttpMessageNotReadableException): ErrorResponse {
        val errorMessage = ex.localizedMessage?.substringBefore("\n") ?: "Invalid request body"

        return ErrorResponse(
            errorMessage = errorMessage,
            errors = mapOf("body" to errorMessage),
        )
    }

    @ExceptionHandler(BusinessRuleViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handle400BusinessRuleViolationException(ex: BusinessRuleViolationException): ErrorResponse =
        ErrorResponse(
            errorMessage = ex.message,
            errors = ex.errors,
        )

    @ExceptionHandler(WebClientResponseException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handle500WebClientResponseException(e: WebClientResponseException): ErrorResponse {
        logger.error("Error calling external API: ${e.statusCode} - ${e.responseBodyAsString}", e)

        val errorMessage = "Error calling external API: ${e.statusCode} - ${e.responseBodyAsString}"

        return ErrorResponse(
            errorMessage = errorMessage,
        )
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GlobalControllerAdvice::class.java)
    }
}
