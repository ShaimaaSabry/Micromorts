package com.miradx.shaimaa.micromorts.infrastructure.config

import com.miradx.shaimaa.micromorts.application.commands.calculaterisk.CalculateRiskHandler
import com.miradx.shaimaa.micromorts.application.commands.createactivities.CreateActivitiesHandler
import com.miradx.shaimaa.micromorts.application.contracts.ActivityRepository
import com.miradx.shaimaa.micromorts.application.queries.GetActivitiesHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfig {

    @Bean
    fun calculateRiskHandler(activityRepository: ActivityRepository): CalculateRiskHandler {
        return CalculateRiskHandler(activityRepository)
    }

    @Bean
    fun createActivitiesHandler(activityRepository: ActivityRepository): CreateActivitiesHandler {
        return CreateActivitiesHandler(activityRepository)
    }

    @Bean
    fun getActivitiesHandler(activityRepository: ActivityRepository): GetActivitiesHandler {
        return GetActivitiesHandler(activityRepository)
    }
}
