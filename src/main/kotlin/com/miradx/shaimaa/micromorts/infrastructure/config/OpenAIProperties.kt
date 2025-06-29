package com.miradx.shaimaa.micromorts.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "gateways.openai")
class OpenAIProperties {
    lateinit var url: String
    lateinit var apiKey: String
}
