package com.miradx.shaimaa.micromorts.presentation.api.v1.activity

import com.fasterxml.jackson.databind.ObjectMapper
import com.miradx.shaimaa.micromorts.application.commands.createactivities.CreateActivitiesHandler
import com.miradx.shaimaa.micromorts.application.queries.getactivities.GetActivitiesHandler
import com.miradx.shaimaa.micromorts.domain.model.ActionUnit
import com.miradx.shaimaa.micromorts.domain.model.Activity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.stream.Stream

@WebMvcTest(ActivityController::class)
@Import(ActivityControllerTest.MockedBeans::class)
class ActivityControllerTest {
    @TestConfiguration
    class MockedBeans {
        @Bean fun createActivitiesHandler(): CreateActivitiesHandler = mockk(relaxed = true)

        @Bean fun getActivitiesHandler(): GetActivitiesHandler = mockk()
    }

    @Autowired lateinit var mockGetActivitiesHandler: GetActivitiesHandler

    @Autowired lateinit var mockCreateActivitiesHandler: CreateActivitiesHandler

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var objectMapper: ObjectMapper

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class GetActivities {
        @Test
        fun `should return 200 OK with list of activities`() {
            // given
            val activities =
                listOf(
                    Activity(
                        activity = "skiing",
                        quantity = 1f,
                        unit = ActionUnit.QUANTITY,
                        micromorts = 0.7f,
                    ),
                    Activity(
                        activity = "cycling",
                        quantity = 2f,
                        unit = ActionUnit.MINUTE,
                        micromorts = 0.2f,
                    ),
                )
            every { mockGetActivitiesHandler.invoke() } returns activities

            // when & then
            mockMvc
                .get("/v1/activities")
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.activities[0].activity") { value(activities[0].activity) }
                    jsonPath("$.activities[0].quantity") { value(activities[0].quantity) }
                    jsonPath("$.activities[0].unit") { value(activities[0].unit.name) }
                    jsonPath("$.activities[0].micromorts") { value(activities[0].micromorts) }
                    jsonPath("$.activities[1].activity") { value(activities[1].activity) }
                    jsonPath("$.activities[1].quantity") { value(activities[1].quantity) }
                    jsonPath("$.activities[1].unit") { value(activities[1].unit.name) }
                    jsonPath("$.activities[1].micromorts") { value(activities[1].micromorts) }
                }
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class CreateActivities {
        @Test
        fun `should create activities and return 201 Created`() {
            // given
            val request =
                mapOf(
                    "activities" to
                        listOf(
                            mapOf(
                                "activity" to "skiing",
                                "quantity" to 1f,
                                "unit" to "quantity",
                                "micromorts" to 0.7f,
                            ),
                            mapOf(
                                "activity" to "cycling",
                                "quantity" to 2f,
                                "unit" to "minute",
                                "micromorts" to 0.2f,
                            ),
                        ),
                )

            // when & then
            mockMvc
                .post("/v1/activities") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                }

            // then
            verify(exactly = 1) { mockCreateActivitiesHandler.invoke(any()) }
        }

        @ParameterizedTest
        @MethodSource("invalidActivityRequests")
        fun `given invalid request, then returns 400 Bad Request`(
            request: CreateActivitiesRequest,
            expectedErrorMessage: String,
            expectedErrors: Map<String, String>,
        ) {
            // when, then
            mockMvc
                .post("/v1/activities") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$.errorMessage") { value(expectedErrorMessage) }
                        expectedErrors.forEach { (field, message) ->
                            jsonPath("$.errors['$field']") { value(message) }
                        }
                    }
                }
        }

        private fun invalidActivityRequests(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    CreateActivitiesRequest(activities = null),
                    "activities must not be null",
                    mapOf("activities" to "must not be null"),
                ),
                Arguments.of(
                    CreateActivitiesRequest(activities = emptyList()),
                    "activities size must be between 1 and 2147483647",
                    mapOf("activities" to "size must be between 1 and 2147483647"),
                ),
                Arguments.of(
                    CreateActivitiesRequest(
                        activities =
                            listOf(
                                ActivityDto(
                                    activity = null,
                                    quantity = null,
                                    unit = null,
                                    micromorts = null,
                                ),
                            ),
                    ),
                    "Request validation errors",
                    mapOf(
                        "activities[0].activity" to "must not be blank",
                        "activities[0].unit" to "must not be blank",
                        "activities[0].quantity" to "must not be null",
                        "activities[0].micromorts" to "must not be null",
                    ),
                ),
                Arguments.of(
                    CreateActivitiesRequest(
                        activities =
                            listOf(
                                ActivityDto(
                                    activity = "",
                                    quantity = 0f,
                                    unit = "",
                                    micromorts = -0.5f,
                                ),
                            ),
                    ),
                    "Request validation errors",
                    mapOf(
                        "activities[0].activity" to "must not be blank",
                        "activities[0].unit" to "must not be blank",
                        "activities[0].quantity" to "must be greater than 0",
                        "activities[0].micromorts" to "must be greater than or equal to 0",
                    ),
                ),
            )
    }
}
