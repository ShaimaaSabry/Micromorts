package com.miradx.shaimaa.micromorts.infrastructure.gateways.openai

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.miradx.shaimaa.micromorts.infrastructure.config.OpenAIProperties
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EmbeddingProviderTest {
    private lateinit var embeddingProvider: EmbeddingProvider

    private lateinit var wireMockServer: WireMockServer
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        val port = 8089
        wireMockServer = WireMockServer(port)
        wireMockServer.start()

        val props =
            OpenAIProperties().apply {
                url = "http://localhost:$port"
                apiKey = "test-api-key"
            }
        embeddingProvider = EmbeddingProvider(props)
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.stop()
    }

    @Test
    fun `should call OpenAI embedding API and return embedding`() {
        // Given
        val response =
            OpenAIEmbeddingResponse(
                data =
                    listOf(
                        EmbeddingData(
                            embedding = listOf(0.1f, 0.2f, 0.3f),
                        ),
                    ),
            )

        val responseJson = objectMapper.writeValueAsString(response)

        wireMockServer.stubFor(
            post(urlEqualTo("/v1/embeddings"))
                .withHeader("Authorization", equalTo("Bearer test-api-key"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJson),
                ),
        )

        // When
        val result = embeddingProvider.embed("Hello world")

        // Then
        assertEquals(listOf(0.1f, 0.2f, 0.3f), result)
    }
}
