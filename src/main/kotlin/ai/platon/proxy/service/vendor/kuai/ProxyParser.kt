package ai.platon.proxy.service.vendor.kuai

import ai.platon.proxy.service.vendor.ProxyParser
import ai.platon.pulsar.common.proxy.ProxyEntry
import com.google.gson.GsonBuilder
import java.net.Proxy
import java.time.Instant

private class ProxyData(
    val count: Int = 0, val proxy_list: List<String> = listOf()
)

private class ProxyResult(
    val code: Int = 0, val msg: String = "0", val data: ProxyData = ProxyData()
)

/**
 * https://www.kuaidaili.com/uc/tps/?orderid=934559448859095&tab=base-info
 * */
class KuaiDaiLiProxyParser : ProxyParser() {
    companion object {
        const val PROVIDER_URL_EXAMPLE =
            "https://tps.kdlapi.com/api/gettps/?secret_id={YOUR-secret_id}&signature=jotm8jn6syleypxqf2yfam85v1e8xqx6&num=1&pt=2&format=json&sep=1"
        val EXAMPLE_RESPONSE = """
{
  "msg": "",
  "code": 0,
  "data": {
    "count": 1,
    "proxy_list": [
      "a585.kdltps.com:20818"
    ]
  }
}
""".trimIndent()
    }

    private val gson = GsonBuilder().create()

    val providerURL = System.getProperty("KUAI-DAI-LI-PROVIDER-URL")

    override val name: String
        get() = "kuaidaili"

    override val providerDescription: String = if (providerURL.isNotBlank()) "$providerURL -vendor $name -fmt json" else ""

    override fun parse(text: String, format: String): List<ProxyEntry> {
        return doParse(text, format)
    }

    private fun doParse(text: String, format: String): List<ProxyEntry> {
        if (format == "json") {
            val result = gson.fromJson(text, ProxyResult::class.java)
            if (result.code == 0) {
                val hostPorts = result.data.proxy_list.map { it.split(":") }.filter { it.size == 2 }
                return hostPorts.map {
                    ProxyEntry(it[0], it[1].toInt()).also {
                        it.type = Proxy.Type.SOCKS
                        it.declaredTTL = Instant.now().plusSeconds(30 * 60)
                    }
                }
            }
        }

        return listOf()
    }
}

fun main() {
    val parser = KuaiDaiLiProxyParser()
    parser.enableProvider()
}
