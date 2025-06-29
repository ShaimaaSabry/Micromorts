package com.miradx.shaimaa.micromorts.infrastructure.repositories.qdrant

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.put
import com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.miradx.shaimaa.micromorts.domain.model.ActionUnit
import com.miradx.shaimaa.micromorts.domain.model.Activity
import com.miradx.shaimaa.micromorts.infrastructure.config.QdrantProperties
import com.miradx.shaimaa.micromorts.infrastructure.gateways.openai.EmbeddingProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivityQdrantRepositoryTest {
    private lateinit var wireMockServer: WireMockServer
    private lateinit var repository: ActivityQdrantRepository
    private val objectMapper = jacksonObjectMapper()
    private val mockEmbeddingProvider = mockk<EmbeddingProvider>()

    @BeforeEach
    fun setUp() {
        wireMockServer = WireMockServer(8088)
        wireMockServer.start()

        val props =
            QdrantProperties().apply {
                url = "http://localhost:8088"
            }

        every { mockEmbeddingProvider.embed(any()) } returns listOf(0.1f, 0.2f, 0.3f)

        repository = ActivityQdrantRepository(props, mockEmbeddingProvider)
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.stop()
    }

    @Nested
    inner class GetActivities {
        @Test
        fun `should retrieve activities from Qdrant`() {
            // Given
            val qdrantResponse =
                QdrantPointsResponse(
                    result =
                        QdrantScrollResult(
                            points =
                                listOf(
                                    QdrantRetrievePoint(
                                        payload = QdrantActivityPayload("skiing", 1f, "QUANTITY", 0.7f),
                                    ),
                                ),
                        ),
                )
            val jsonResponse = objectMapper.writeValueAsString(qdrantResponse)

            wireMockServer.stubFor(
                post(urlEqualTo("/collections/activities/points/scroll"))
                    .willReturn(
                        aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(jsonResponse),
                    ),
            )

            // When
            val result = repository.getActivities()

            // Then
            assertEquals(1, result.size)
            assertEquals("skiing", result[0].activity)
            assertEquals(ActionUnit.QUANTITY, result[0].unit)
        }
    }

    @Nested
    inner class CreateActivities {
        @Test
        fun `should send activities to Qdrant`() {
            // Given
            wireMockServer.stubFor(
                put(urlEqualTo("/collections/activities/points"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"result\": \"ok\"}"),
                    ),
            )

            val activities =
                listOf(
                    Activity("cycling", 2f, ActionUnit.MINUTE, 0.2f),
                )

            // When & Then
            repository.createActivities(activities)

            wireMockServer.verify(
                putRequestedFor(urlEqualTo("/collections/activities/points")),
            )
        }
    }

    @Nested
    inner class SearchActivities {
        @Test
        fun `should search activities using vector and return matching result`() {
            // Given
            val qdrantSearchResponse =
                QdrantSearchResponse(
                    result =
                        listOf(
                            QdrantSearchResult(
                                QdrantActivityPayload("cycling", 2f, "MINUTE", 0.2f),
                            ),
                        ),
                )
            val jsonResponse = objectMapper.writeValueAsString(qdrantSearchResponse)

            wireMockServer.stubFor(
                post(urlEqualTo("/collections/activities/points/search"))
                    .willReturn(
                        aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(jsonResponse),
                    ),
            )

            // When
            val result = repository.searchActivities("cycling")

            // Then
            assertTrue(result.isPresent)
            val activity = result.get()
            assertEquals("cycling", activity.activity)
            assertEquals(ActionUnit.MINUTE, activity.unit)
            assertEquals(0.2f, activity.micromorts)
        }

        @Test
        fun `should return empty when no results are found`() {
            // Given
            val emptyResponse = QdrantSearchResponse(result = emptyList())
            wireMockServer.stubFor(
                post(urlEqualTo("/collections/activities/points/search"))
                    .willReturn(
                        aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(objectMapper.writeValueAsString(emptyResponse)),
                    ),
            )

            // When
            val result = repository.searchActivities("nonexistent")

            // Then
            assertTrue(result.isEmpty)
        }

        @Test
        fun `should return a map of action to optional activity when multiple actions provided`() {
            // Given
            val jsonResponse =
                objectMapper.writeValueAsString(
                    QdrantSearchResponse(
                        result =
                            listOf(
                                QdrantSearchResult(
                                    QdrantActivityPayload("cycling", 2f, "MINUTE", 0.2f),
                                ),
                            ),
                    ),
                )

            wireMockServer.stubFor(
                post(urlEqualTo("/collections/activities/points/search"))
                    .willReturn(
                        aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(jsonResponse),
                    ),
            )

            val actions = listOf("cycling", "skiing")

            // When
            val resultMap = repository.searchActivities(actions)

            // Then
            assertEquals(2, resultMap.size)
            assertTrue(resultMap["cycling"]?.isPresent == true)
            assertEquals("cycling", resultMap["cycling"]?.get()?.activity)

            // since stubbed response is same for both calls
            assertTrue(resultMap["skiing"]?.isPresent == true)
            assertEquals("cycling", resultMap["skiing"]?.get()?.activity)
        }

        @Test
        fun `should return empty optionals if qdrant returns no result`() {
            // Given
            val emptyResponse =
                objectMapper.writeValueAsString(
                    QdrantSearchResponse(result = emptyList()),
                )

            wireMockServer.stubFor(
                post(urlEqualTo("/collections/activities/points/search"))
                    .willReturn(
                        aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(emptyResponse),
                    ),
            )

            val actions = listOf("unknown1", "unknown2")

            // When
            val resultMap = repository.searchActivities(actions)

            // Then
            assertEquals(2, resultMap.size)
            assertTrue(resultMap["unknown1"]?.isEmpty == true)
            assertTrue(resultMap["unknown2"]?.isEmpty == true)
        }
    }
}
