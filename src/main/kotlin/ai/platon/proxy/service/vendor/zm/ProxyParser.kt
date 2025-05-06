package ai.platon.proxy.service.vendor.zm

import ai.platon.proxy.service.vendor.ProxyParser
import ai.platon.proxy.service.vendor.luna.LunaProxyParser
import ai.platon.pulsar.common.AppPaths
import ai.platon.pulsar.common.DateTimeDetector
import ai.platon.pulsar.common.proxy.ProxyEntry
import ai.platon.pulsar.common.proxy.ProxyInsufficientBalanceException
import ai.platon.pulsar.common.proxy.ProxyRetryException
import ai.platon.pulsar.common.proxy.ProxyVendorException
import ai.platon.pulsar.common.warnInterruptible
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.time.Instant
import java.util.regex.Pattern

private class ProxyItem(
    val ip: String = "",
    val port: Int = 0,
    val expire_time: String = "",
    val city: String = "",
    val isp: String = "",
    val outip: String = ""
)

private class ProxyResult(
    val code: Int = 0,
    val msg: String = "0",
    val success: Boolean = false,
    val data: List<ProxyItem> = listOf()
)

/**
 * http://www.zhimaruanjian.com/
 * */
class ZMProxyParser: ProxyParser() {
    companion object {
        const val PARAM_PROXY_ZM_API_KEY = "proxy.zm.api.key"
    }

    private val logger = LoggerFactory.getLogger(ZMProxyParser::class.java)
    private val gson = GsonBuilder().create()
    private val dateTimeDetector = DateTimeDetector()

    override val name: String
        get() = "zm"

    override fun parse(text: String, format: String): List<ProxyEntry> {
        return doParse(text, format)
    }

    private fun doParse(text: String, format: String): List<ProxyEntry> {
        val apiKey = getApiKey() ?: "{YOUR-API-KEY}"

        if (format == "json") {
            val result = gson.fromJson(text, ProxyResult::class.java)
            if (result.success) {
                return result.data.map { data -> ProxyEntry(data.ip, data.port).also {
                    it.outIp = data.outip.takeIf { it.isNotBlank() } ?: data.ip
                    it.declaredTTL = parseInstant(data.expire_time)
                }}
            }

            if (result.code != 0) {
                when (result.code) {
                    113, 117 -> {
                        val ip = extractIp(result.msg)
                        val link = "https://wapi.http.linkudp.com/index/index/save_white?neek=76534&appkey=$apiKey&white=$ip"
                        logger.warn(result.msg + " using one of the following link:\n$link\n$text")
                        throw ProxyVendorException("Proxy vendor exception, please add $ip to the vendor's while list")
                    }
                    115 -> {
                        // retry
                        throw ProxyRetryException("Retry fetching proxy - $text")
                    }
                    121 -> {
                        throw ProxyInsufficientBalanceException("Insufficient balance - $text")
                    }
                    else -> {
                        throw ProxyVendorException("Proxy vendor exception - $text")
                    }
                }
            }
        }

        return listOf()
    }

    private fun getApiKey(): String? {
        return kotlin.runCatching { getApiKey0() }
            .onFailure { warnInterruptible(this, it) }
            .getOrNull()
    }

    @Throws(IOException::class)
    private fun getApiKey0(): String? {
        var apiKey = System.getProperty(PARAM_PROXY_ZM_API_KEY)
        if (apiKey.isNullOrBlank()) {
            val path = AppPaths.PROXY_BASE_DIR.resolve("$PARAM_PROXY_ZM_API_KEY.txt")
            if (Files.exists(path)) {
                apiKey = Files.readAllLines(path).firstOrNull()
            }
        }
        return apiKey
    }

    private fun extractIp(ipString: String): String {
        val pattern = Pattern.compile(IPADDRESS_PATTERN)
        val matcher = pattern.matcher(ipString)
        return if (matcher.find()) {
            matcher.group()
        } else {
            "0.0.0.0"
        }
    }

    private fun parseInstant(str: String): Instant {
        return dateTimeDetector.parseDateTimeStrictly(str).toInstant()
    }
}
