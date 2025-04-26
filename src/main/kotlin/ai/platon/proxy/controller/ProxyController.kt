package ai.platon.proxy.controller

import ai.platon.proxy.ProxyVendorLoader
import ai.platon.pulsar.common.proxy.ProxyEntry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/api")
class ProxyController(private val proxyVendorLoader: ProxyVendorLoader) {

    @GetMapping("/get-proxy")
    fun getProxy(): Map<String, Any> {
        return try {
            val proxies = proxyVendorLoader.updateProxies(Duration.ofSeconds(30))
            mapOf(
                "status" to "success",
                "message" to "Proxies retrieved successfully",
                "data" to mapOf(
                    "proxies" to proxies.map { proxy ->
                        mapOf(
                            "host" to proxy.host,
                            "port" to proxy.port,
                            "username" to proxy.username,
                            "password" to proxy.password,
                            "type" to proxy.type,
                            "expiresAt" to proxy.declaredTTL
                        )
                    }
                )
            )
        } catch (e: Exception) {
            mapOf(
                "status" to "error",
                "message" to "Failed to retrieve proxies: ${e.message}",
                "data" to emptyMap<String, Any>()
            )
        }
    }

    @GetMapping("/get-moke-proxy")
    fun getMockProxy(): Map<String, Any> {
        return try {
            val proxies = listOf(
                ProxyEntry(
                    "127.0.0.1",
                    10908,
                )
            )
            mapOf(
                "status" to "success",
                "message" to "Proxies retrieved successfully",
                "data" to mapOf(
                    "proxies" to proxies.map { proxy ->
                        mapOf(
                            "host" to proxy.host,
                            "port" to proxy.port,
                            "username" to proxy.username,
                            "password" to proxy.password,
                            "type" to proxy.type,
                            "expiresAt" to proxy.declaredTTL
                        )
                    }
                )
            )
        } catch (e: Exception) {
            mapOf(
                "status" to "error",
                "message" to "Failed to retrieve proxies: ${e.message}",
                "data" to emptyMap<String, Any>()
            )
        }
    }
}
