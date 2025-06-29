package com.miradx.shaimaa.micromorts.infrastructure.repositories.qdrant

data class QdrantActivityPayload(
    val activity: String,
    val quantity: Float,
    val unit: String,
    val micromorts: Float,
)
