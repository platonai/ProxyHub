package ai.platon.proxy

import ai.platon.proxy.repository.ProxyProviderRepository
import ai.platon.proxy.repository.UserRepository
import ai.platon.proxy.service.ProxyVendorLoader
import ai.platon.pulsar.external.ChatModelFactory
import ai.platon.pulsar.skeleton.context.PulsarContexts
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class ProxyApplication(
    private val proxyVendorLoader: ProxyVendorLoader,
    private val proxyProviderRepository: ProxyProviderRepository,
) {
    private val session = PulsarContexts.createSession()

    @Value("\${deepseek.api.key}")
    lateinit var deepseekAPIKey: String

    @PostConstruct
    fun initLLM() {
        System.setProperty("deepseek.api.key", deepseekAPIKey)

        // Check if the LLM is available
        // This is a placeholder for the actual check
        val available = ChatModelFactory.isModelConfigured(session.sessionConfig)
        if (available) {
            println("LLM is available")
        } else {
            println("LLM is not available")
        }
    }

    @PostConstruct
    fun initProxyVendorLoader() {
        val providers = proxyProviderRepository.findAll()
        providers.forEach { provider ->
            proxyVendorLoader.addProviderURL(provider.url)
        }
    }
}

fun main(args: Array<String>) {
    SpringApplicationBuilder(ProxyApplication::class.java)
        .registerShutdownHook(true).run(*args)
}
