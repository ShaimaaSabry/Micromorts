package com.miradx.shaimaa.micromorts.infrastructure.repositories.qdrant

data class QdrantSearchResponse(
    val result: List<QdrantSearchResult>,
)

data class QdrantSearchResult(
    val payload: QdrantActivityPayload,
)
