package ai.platon.proxy.vendor.kuai

import ai.platon.proxy.service.ProxyVendorLoader
import ai.platon.proxy.service.vendor.kuai.KuaiDaiLiProxyParser
import ai.platon.pulsar.common.AppPaths
import ai.platon.pulsar.common.config.ImmutableConfig
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KuaiDaiLiProxyParserTest {
    val exampleResponse = """
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

    val parser = KuaiDaiLiProxyParser()

    @Test
    fun testParseProxyEntry() {
        val proxyEntry = parser.parse(exampleResponse, "json")
        println(proxyEntry)
        assert(proxyEntry.isNotEmpty())
        assert(proxyEntry[0].host == "a585.kdltps.com")
        assert(proxyEntry[0].port == 20818)
    }

    @Test
    fun testEnableProvider() {
        parser.disableProvider()
        assertFalse { Files.exists(AppPaths.ENABLED_PROVIDER_DIR.resolve(parser.name + ".txt")) }
        parser.enableProvider()
        assertTrue { Files.exists(AppPaths.ENABLED_PROVIDER_DIR.resolve(parser.name + ".txt")) }
    }

    @Test
    fun testFetchFromProvider() {
        Assumptions.assumeTrue(parser.providerURL.isNotBlank())

        parser.disableProvider()
        val loader = ProxyVendorLoader(ImmutableConfig())
        val proxyEntry = loader.fetchProxiesFromProvider(parser.providerDescription)
        println(proxyEntry)
        assert(proxyEntry.isNotEmpty())
        // assert(proxyEntry[0].host == "a585.kdltps.com")
    }
}
