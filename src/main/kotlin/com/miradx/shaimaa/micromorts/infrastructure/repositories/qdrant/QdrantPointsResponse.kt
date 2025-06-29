package com.miradx.shaimaa.micromorts.infrastructure.repositories.qdrant

data class QdrantPointsResponse(
    val result: QdrantScrollResult,
)

data class QdrantScrollResult(
    val points: List<QdrantRetrievePoint>,
)

data class QdrantRetrievePoint(
    val payload: QdrantActivityPayload,
)
