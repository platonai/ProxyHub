package ai.platon.proxy.service.vendor.luna

import ai.platon.proxy.service.vendor.ProxyParser
import ai.platon.pulsar.common.proxy.ProxyEntry
import java.time.Duration
import java.time.Instant

/**
 * https://www.lunaproxy.com/
 * */
class LunaProxyParser: ProxyParser() {
    override val name: String
        get() = "luna"

    override fun parse(text: String, format: String): List<ProxyEntry> {
        if (text.contains("Allowlist verification failed")) {
            logger.warn(text)
            logger.warn("https://gapi.lunaproxy.com/index/index/save_allowlist?neek=1067088&appkey=30964dc980c89b3d91b3ee6b2c5e1d44&white=Your ip (please separate multiple ips with English commas)")
            return listOf()
        }

        val text0 = text.trim()
        logger.info("Proxies text: >>{}<<", text0)
        val proxyStrings = text0.split("\n").map { it.trim() }.filter { it.contains(":") }
        val ttl = Instant.now().plus(Duration.ofMinutes(30))
        return proxyStrings.mapNotNull { ProxyEntry.parse(it)?.also { it.declaredTTL = ttl } }
    }
}
