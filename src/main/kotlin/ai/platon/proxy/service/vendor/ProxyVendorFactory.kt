package ai.platon.proxy.service.vendor

import ai.platon.proxy.service.vendor.kuai.KuaiDaiLiProxyParser
import ai.platon.proxy.service.vendor.luna.LunaProxyParser
import ai.platon.proxy.service.vendor.proxy_seller.ProxySellerProxyParser
import ai.platon.proxy.service.vendor.zm.ZMProxyParser
import ai.platon.pulsar.common.AppPaths
import ai.platon.pulsar.common.Strings
import ai.platon.pulsar.common.proxy.ProxyEntry
import ai.platon.pulsar.common.serialize.json.pulsarObjectMapper
import ai.platon.pulsar.common.urls.UrlUtils
import ai.platon.pulsar.skeleton.context.PulsarContexts
import org.apache.commons.lang3.math.NumberUtils
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteIfExists

abstract class ProxyParser {
    companion object {
        const val IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
    }

    val logger = LoggerFactory.getLogger(ProxyParser::class.java)

    abstract val name: String

    open val providerDescription = ""

    fun enableProvider() {
        if (providerDescription.isBlank()) {
            logger.warn("Provider URL is blank")
            return
        }

        if (!UrlUtils.isStandard(providerDescription)) {
            logger.warn("Unknown provider URL: $providerDescription")
            return
        }

        var path = AppPaths.AVAILABLE_PROVIDER_DIR.resolve("$name.txt")
        Files.writeString(path, providerDescription)

        path = AppPaths.ENABLED_PROVIDER_DIR.resolve("$name.txt")
        Files.writeString(path, providerDescription)
    }

    fun disableProvider() {
        val path = AppPaths.ENABLED_PROVIDER_DIR.resolve("$name.txt")
        path.deleteIfExists()
    }

    abstract fun parse(text: String, format: String = "json"): List<ProxyEntry>
    open fun parse(path: Path, format: String): List<ProxyEntry> = parse(Files.readString(path), format)
}

class UniversalProxyParser: ProxyParser() {
    private val session = PulsarContexts.createSession()
    private val prompt = """
Extract proxies from the text, and return them in JSON format:

```json
{
    status: "the status of the response, it can be one of the following: [success, failure]",
    host: "the extracted host, it can be an IP address or a domain name",
    port: "the extracted port, it should be an integer",
}
```

Your response should contains ONLY the JSON object, and nothing else.

    """.trimIndent()

    override val name: String
        get() = "universal"

    override fun parse(text: String, format: String): List<ProxyEntry> {
        val response = session.chat(prompt, text).content

        val jsonText = response.substringAfter("```json").substringBeforeLast("```")

        val json = pulsarObjectMapper().readTree(jsonText)

        if (json.has("status") && json.has("host") && json.has("port")) {
            val status = json.get("status").asText()
            val host = json.get("host").asText()
            val port = json.get("port").asText()

            if (status == "success" && Strings.isNumericLike(port)) {
                return listOf(ProxyEntry(host, port.toInt()))
            } else {
                logger.warn("Invalid proxy entry: $response")
            }
        } else {
            logger.warn("Failed to extract proxy entry: $response")
        }
        return listOf()
    }

    override fun parse(path: Path, format: String): List<ProxyEntry> {
        return listOf()
    }
}

object ProxyVendorFactory {
    fun getProxyParser(vendor: String): ProxyParser {
        return when (vendor) {
            "kuaidaili" -> KuaiDaiLiProxyParser()
            "zm" -> ZMProxyParser()
            "luna" -> LunaProxyParser()
            "ps" -> ProxySellerProxyParser()
            "proxy-seller" -> ProxySellerProxyParser()
            else -> UniversalProxyParser()
        }
    }
}
