package com.miradx.shaimaa.micromorts.presentation.api.v1.activity

import com.miradx.shaimaa.micromorts.application.commands.createactivities.CreateActivitiesHandler
import com.miradx.shaimaa.micromorts.application.queries.getactivities.GetActivitiesHandler
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/activities")
@Tag(name = "Activities API", description = "Manage activities in the platform data store.")
class ActivityController(
    private val createActivitiesHandler: CreateActivitiesHandler,
    private val getActivitiesHandler: GetActivitiesHandler,
) {
    @GetMapping
    fun getActivities(): GetActivitiesResponse {
        val activities = this.getActivitiesHandler()
        return GetActivitiesResponse.from(activities)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createActivities(
        @Valid @RequestBody
        request: CreateActivitiesRequest,
    ) {
        this.createActivitiesHandler(request.toCommand())
    }
}
