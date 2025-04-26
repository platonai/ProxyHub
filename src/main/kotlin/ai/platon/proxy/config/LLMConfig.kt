package ai.platon.proxy.config

import ai.platon.pulsar.external.ChatModelFactory
import ai.platon.pulsar.skeleton.context.PulsarContexts
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class LLMConfig(
    private val environment: Environment
) {
    private val session = PulsarContexts.createSession()

    @PostConstruct
    fun initLLM() {

        session.unmodifiedConfig.environment = environment
//        System.setProperty("DEEPSEEK_API_KEY", deepseekAPIKey)

        // Check if the LLM is available
        // This is a placeholder for the actual check
        val available = ChatModelFactory.isModelConfigured(session.sessionConfig)
        if (available) {
            println("LLM is available")
        } else {
            println("LLM is not available")
        }
    }
}
