package com.miradx.shaimaa.micromorts.presentation.api.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException

@Component
class LoggingFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestWrapper = ContentCachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)

        filterChain.doFilter(requestWrapper, responseWrapper)

        logRequest(requestWrapper)
        logResponse(responseWrapper)
    }

    fun logRequest(requestWrapper: ContentCachingRequestWrapper) {
        val method = requestWrapper.method
        val url = requestWrapper.requestURI
        val body = requestWrapper.contentAsString

        LOG.info("Request: {} {}: {}", method, url, body)
    }

    @Throws(IOException::class)
    fun logResponse(responseWrapper: ContentCachingResponseWrapper) {
        val status = responseWrapper.status.toString()
        val body = String(responseWrapper.contentAsByteArray, charset(responseWrapper.characterEncoding))

        LOG.info("Response: {}: {}", status, body)

        responseWrapper.copyBodyToResponse()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(LoggingFilter::class.java)
    }
}
