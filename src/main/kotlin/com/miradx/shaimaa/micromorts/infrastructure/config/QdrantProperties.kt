package com.miradx.shaimaa.micromorts.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "gateways.qdrant")
class QdrantProperties {
    lateinit var url: String
}
