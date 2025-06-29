package com.miradx.shaimaa.micromorts.infrastructure.repository

import com.miradx.shaimaa.micromorts.infrastructure.config.OpenAIProperties
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class EmbeddingProvider(
    private val properties: OpenAIProperties
) {
    private val client = WebClient.create(properties.url)

    fun embed(text: String): List<Float> {
        val apiKey = properties.apiKey

        val response = client.post()
            .uri("/v1/embeddings")
            .header("Authorization", "Bearer $apiKey")
            .bodyValue(
                mapOf(
                    "input" to text,
                    "model" to "text-embedding-3-small"
                )
            )
            .retrieve()
            .bodyToMono(OpenAIEmbeddingResponse::class.java)
            .block()

        return response?.data?.firstOrNull()?.embedding ?: emptyList()
    }

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(EmbeddingProvider::class.java)
    }
}

data class OpenAIEmbeddingResponse(
    val data: List<EmbeddingData>
)

data class EmbeddingData(
    val embedding: List<Float>
)
