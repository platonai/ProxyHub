package ai.platon.proxy.service.vendor.proxy_seller

import ai.platon.proxy.service.vendor.ProxyParser
import ai.platon.pulsar.common.DateTimeDetector
import ai.platon.pulsar.common.proxy.ProxyEntry
import com.google.gson.GsonBuilder
import java.time.Instant

private class MobileProxyItem(
    val id: String = "",
    val order_id: String = "",
    val basket_id: String = "",
    val ip: String = "",
    val protocol: String = "",
    val ip_only: String = "",
    val port_socks: Int = 0,
    val port_http: Int = 0,
    val login: String = "",
    val password: String = "",
    val auth_ip: String = "",
    val rotation: String = "",
    val link_reboot: String = "",
    val country: String = "",
    val country_alpha3: String = "",
    val status: String = "",
    val status_type: String = "",
    val can_prolong: String = "",
    val date_start: String = "",
    val date_end: String = ""
)

private class ProxyData(
    val mobile: List<MobileProxyItem> = listOf()
)

private class ProxyResult(
    val status: String = "",
    val data: ProxyData? = null,
    val errors: List<String> = listOf(),
)

/**
 * https://proxy-seller.com/
 * */
class ProxySellerProxyParser: ProxyParser() {
    private var rotateIpLink = ""

    private val gson = GsonBuilder().create()
    private val dateTimeDetector = DateTimeDetector()

    override val name: String
        get() = "proxy-seller"

    override fun parse(text: String, format: String): List<ProxyEntry> {
        return doParse(text, format)
    }

    private fun doParse(text: String, format: String): List<ProxyEntry> {
        val result = gson.fromJson(text, ProxyResult::class.java)
        if (result.errors.isEmpty()) {
            val data = result.data
            if (data != null) {
                return data.mobile.map { data -> ProxyEntry(data.ip_only, data.port_http).also {
                    it.outIp = data.ip_only
                    it.declaredTTL = parseInstant(data.date_end)
                }}
            }
        }

        return listOf()
    }

    private fun parseInstant(str: String): Instant {
        return dateTimeDetector.parseDateStrictly(str, "dd.MM.yyyy").toInstant()
    }
}
