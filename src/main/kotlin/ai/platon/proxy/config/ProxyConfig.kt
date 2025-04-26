package ai.platon.proxy.config

import ai.platon.proxy.service.ProxyVendorLoader
import ai.platon.pulsar.common.config.ImmutableConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProxyConfig {

    @Bean
    fun proxyVendorLoader(): ProxyVendorLoader {
        val config = ImmutableConfig()
        return ProxyVendorLoader(config)
    }
}
