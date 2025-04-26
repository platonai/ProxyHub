package ai.platon.proxy.vendor

import ai.platon.proxy.service.vendor.UniversalProxyParser
import kotlin.test.Test

class UniversalProxyParserTest {
    @Test
    fun testParse() {
        val parser = UniversalProxyParser()
        val text = """
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
        val proxyEntry = parser.parse(text, "json")
        println(proxyEntry)
        assert(proxyEntry.isNotEmpty())
        assert(proxyEntry[0].host == "a585.kdltps.com")
        assert(proxyEntry[0].port == 20818)
    }
}
