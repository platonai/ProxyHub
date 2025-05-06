package ai.platon.proxy.service.vendor

import ai.platon.proxy.service.vendor.kuai.KuaiDaiLiProxyParser
import ai.platon.proxy.service.vendor.luna.LunaProxyParser
import ai.platon.proxy.service.vendor.proxy_seller.ProxySellerProxyParser
import ai.platon.proxy.service.vendor.zm.ZMProxyParser
import ai.platon.pulsar.common.AppPaths
import ai.platon.pulsar.common.proxy.ProxyEntry
import ai.platon.pulsar.common.urls.UrlUtils
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

object ProxyVendorFactory {
    fun getProxyParser(vendor: String): ProxyParser {
        return when (vendor) {
            "kuaidaili-disabled" -> KuaiDaiLiProxyParser()
            "zm" -> ZMProxyParser()
            "luna" -> LunaProxyParser()
            "ps" -> ProxySellerProxyParser()
            "proxy-seller" -> ProxySellerProxyParser()
            else -> UniversalProxyParser()
        }
    }
}
