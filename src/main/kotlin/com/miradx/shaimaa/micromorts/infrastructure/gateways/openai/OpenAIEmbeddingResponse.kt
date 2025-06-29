package com.miradx.shaimaa.micromorts.infrastructure.gateways.openai

data class OpenAIEmbeddingResponse(
    val data: List<EmbeddingData>,
)

data class EmbeddingData(
    val embedding: List<Float>,
)
