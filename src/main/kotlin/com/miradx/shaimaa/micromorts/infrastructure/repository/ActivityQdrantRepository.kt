package com.miradx.shaimaa.micromorts.infrastructure.repository

import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import com.miradx.shaimaa.micromorts.domain.model.ActionUnit
import com.miradx.shaimaa.micromorts.domain.model.Activity
import com.miradx.shaimaa.micromorts.infrastructure.config.QdrantProperties
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Primary
@Component
class ActivityQdrantRepository(
    properties: QdrantProperties,
    val embeddingProvider: EmbeddingProvider
) : ActivityRepository {
    private val webClient = WebClient.builder()
        .baseUrl(properties.url)
        .build()

    override fun getActivities(): List<Activity> {
        val response = webClient.post()
            .uri("/collections/activities/points/scroll")
            .bodyValue(
                mapOf(
                    "with_payload" to true,
                    "with_vector" to false
                )
            )
            .retrieve()
            .bodyToMono(QdrantPointsResponse::class.java)
            .block() ?: return emptyList()

        return response.result.points.map { point ->
            val payload = point.payload
            Activity(
                activity = payload.activity,
                quantity = payload.quantity,
                unit = ActionUnit.valueOf(payload.unit),
                miromorts = payload.micromorts
            )
        }
    }

    override fun createActivities(activities: List<Activity>) {
        val points = activities.map { activity ->
            QdrantCreatePoint(
                id = UUID.randomUUID().toString(),
                vector = embeddingProvider.embed(activity.activity),
                payload = QdrantActivityPayload(
                    activity = activity.activity,
                    quantity = activity.quantity,
                    unit = activity.unit.name,
                    micromorts = activity.miromorts
                )
            )
        }
        val requestBody = mapOf("points" to points)

        val response = webClient.put()
            .uri("/collections/activities/points")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        logger.debug("Qdrant response: $response")
    }

    override fun searchActivities(action: String): Optional<Activity> {
        val requestBody = mapOf(
            "vector" to embeddingProvider.embed(action),
            "top" to 1,
            "with_payload" to true
        )

        val response = webClient.post()
            .uri("/collections/activities/points/search")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(QdrantSearchResponse::class.java)
            .block()

        val payload = response?.result?.firstOrNull()?.payload ?: return Optional.empty()

        return Optional.of(
            Activity(
                activity = payload.activity,
                quantity = payload.quantity,
                unit = ActionUnit.valueOf(payload.unit),
                miromorts = payload.micromorts
            )
        )
    }

    override fun searchActivities(actions: List<String>): Map<String, Optional<Activity>> = runBlocking {
        actions.map { action ->
            async {
                val requestBody = mapOf(
                    "vector" to embeddingProvider.embed(action),
                    "top" to 1,
                    "with_payload" to true
                )

                val response = webClient.post()
                    .uri("/collections/activities/points/search")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(QdrantSearchResponse::class.java)
                    .block()

                val payload = response?.result?.firstOrNull()?.payload

                action to if (payload != null) {
                    Optional.of(
                        Activity(
                            activity = payload.activity,
                            quantity = payload.quantity,
                            unit = ActionUnit.valueOf(payload.unit),
                            miromorts = payload.micromorts
                        )
                    )
                } else {
                    Optional.empty()
                }
            }
        }.awaitAll().toMap()
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ActivityQdrantRepository::class.java)
    }
}

data class QdrantPointsResponse(
    val result: QdrantScrollResult
)

data class QdrantScrollResult(
    val points: List<QdrantRetrievePoint>
)

data class QdrantRetrievePoint(
    val payload: QdrantActivityPayload
)

data class QdrantActivityPayload(
    val activity: String,
    val quantity: Float,
    val unit: String,
    val micromorts: Float
)

data class QdrantCreatePoint(
    val id: String,
    val vector: List<Float>,
    val payload: QdrantActivityPayload
)

data class QdrantSearchResponse(
    val result: List<QdrantSearchResult>
)

data class QdrantSearchResult(
    val payload: QdrantActivityPayload
)
