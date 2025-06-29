package com.miradx.shaimaa.micromorts.infrastructure.repositories.qdrant

data class QdrantCreatePointRequest(
    val id: String,
    val vector: List<Float>,
    val payload: QdrantActivityPayload,
)
