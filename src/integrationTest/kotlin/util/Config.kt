package com.miradx.shaimaa.micromorts.api.util

import org.yaml.snakeyaml.Yaml

object Config {
    private const val DEFAULT_ENV = "local"
    private lateinit var config: Map<String, Any>

    private fun loadConfig() {
        if (!::config.isInitialized) {
            val env = System.getenv("ENV") ?: DEFAULT_ENV
            val configFile = "application-$env.yaml"

            val inputStream =
                Config::class.java.classLoader.getResourceAsStream(configFile)
                    ?: throw IllegalArgumentException("Configuration file not found: $configFile")

            val yaml = Yaml()
            config = yaml.load(inputStream) as Map<String, Any>
        }
    }

    @JvmStatic
    fun targetUrl(): String {
        loadConfig()
        return config["targetUrl"] as String
    }
}
