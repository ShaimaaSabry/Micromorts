package com.miradx.shaimaa.micromorts.infrastructure.repositories.qdrant

import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import com.miradx.shaimaa.micromorts.domain.model.ActionUnit
import com.miradx.shaimaa.micromorts.domain.model.Activity
import com.miradx.shaimaa.micromorts.infrastructure.config.QdrantProperties
import com.miradx.shaimaa.micromorts.infrastructure.gateways.openai.EmbeddingProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.security.MessageDigest
import java.util.Optional
import java.util.UUID

@Primary
@Component
class ActivityQdrantRepository(
    properties: QdrantProperties,
    val embeddingProvider: EmbeddingProvider,
) : ActivityRepository {
    private val webClient =
        WebClient
            .builder()
            .baseUrl(properties.url)
            .build()

    override fun getActivities(): List<Activity> {
        val response =
            webClient
                .post()
                .uri("/collections/activities/points/scroll")
                .bodyValue(
                    mapOf(
                        "with_payload" to true,
                        "with_vector" to false,
                    ),
                ).retrieve()
                .bodyToMono(QdrantPointsResponse::class.java)
                .block() ?: return emptyList()

        return response.result.points.map { point ->
            val payload = point.payload
            Activity(
                activity = payload.activity,
                quantity = payload.quantity,
                unit = ActionUnit.valueOf(payload.unit),
                micromorts = payload.micromorts,
            )
        }
    }

    override fun createActivities(activities: List<Activity>) {
        val points =
            activities.map { activity ->
                QdrantCreatePointRequest(
                    id = uuidFromString(activity.activity),
                    vector = embeddingProvider.embed(activity.activity),
                    payload =
                        QdrantActivityPayload(
                            activity = activity.activity,
                            quantity = activity.quantity,
                            unit = activity.unit.name,
                            micromorts = activity.micromorts,
                        ),
                )
            }
        val requestBody = mapOf("points" to points)

        val response =
            webClient
                .put()
                .uri("/collections/activities/points")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

        logger.debug("Qdrant response: $response")
    }

    override fun searchActivities(action: String): Optional<Activity> {
        val requestBody =
            mapOf(
                "vector" to embeddingProvider.embed(action),
                "top" to 1,
                "with_payload" to true,
            )

        val response =
            webClient
                .post()
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
                micromorts = payload.micromorts,
            ),
        )
    }

    override fun searchActivities(actions: List<String>): Map<String, Optional<Activity>> =
        runBlocking {
            actions
                .map { action ->
                    async {
                        val requestBody =
                            mapOf(
                                "vector" to embeddingProvider.embed(action),
                                "top" to 1,
                                "with_payload" to true,
                            )

                        val response =
                            webClient
                                .post()
                                .uri("/collections/activities/points/search")
                                .bodyValue(requestBody)
                                .retrieve()
                                .bodyToMono(QdrantSearchResponse::class.java)
                                .block()

                        val payload = response?.result?.firstOrNull()?.payload

                        action to
                            if (payload != null) {
                                Optional.of(
                                    Activity(
                                        activity = payload.activity,
                                        quantity = payload.quantity,
                                        unit = ActionUnit.valueOf(payload.unit),
                                        micromorts = payload.micromorts,
                                    ),
                                )
                            } else {
                                Optional.empty()
                            }
                    }
                }.awaitAll()
                .toMap()
        }

    private fun uuidFromString(value: String): String {
        val md5 = MessageDigest.getInstance("MD5")
        val hash = md5.digest(value.toByteArray())
        val bb = java.nio.ByteBuffer.wrap(hash)
        return UUID(bb.long, bb.long).toString()
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ActivityQdrantRepository::class.java)
    }
}
