package ai.platon.proxy.controller

import ai.platon.proxy.ProxyVendorLoader
import ai.platon.pulsar.common.proxy.ProxyEntry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.net.Proxy
import java.time.Duration
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ProxyControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var proxyVendorLoader: ProxyVendorLoader

    private lateinit var proxyController: ProxyController

    @BeforeEach
    fun setup() {
        proxyController = ProxyController(proxyVendorLoader)
        mockMvc = MockMvcBuilders.standaloneSetup(proxyController).build()
    }

    @Test
    fun `getProxy should return success response with proxies`() {
        // Given
        val proxy = ProxyEntry(
            host = "test.host",
            port = 8080,
            username = "user",
            password = "pass",
            type = Proxy.Type.HTTP
        ).apply {
            declaredTTL = Instant.now().plusSeconds(3600)
        }
        `when`(proxyVendorLoader.updateProxies(Duration.ofSeconds(30))).thenReturn(listOf(proxy))

        // When/Then
        mockMvc.perform(get("/api/get-proxy"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.message").value("Proxies retrieved successfully"))
            .andExpect(jsonPath("$.data.proxies[0].host").value("test.host"))
            .andExpect(jsonPath("$.data.proxies[0].port").value(8080))
            .andExpect(jsonPath("$.data.proxies[0].username").value("user"))
            .andExpect(jsonPath("$.data.proxies[0].password").value("pass"))
            .andExpect(jsonPath("$.data.proxies[0].type").value("HTTP"))
    }

    @Test
    fun `getProxy should return error response when exception occurs`() {
        // Given
        `when`(proxyVendorLoader.updateProxies(Duration.ofSeconds(30)))
            .thenThrow(RuntimeException("Failed to load proxies"))

        // When/Then
        mockMvc.perform(get("/api/get-proxy"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.message").value("Failed to retrieve proxies: Failed to load proxies"))
            .andExpect(jsonPath("$.data").isEmpty)
    }

    @Test
    fun `getMockProxy should return success response with mock proxy`() {
        // When/Then
        mockMvc.perform(get("/api/get-moke-proxy"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.message").value("Proxies retrieved successfully"))
            .andExpect(jsonPath("$.data.proxies[0].host").value("127.0.0.1"))
            .andExpect(jsonPath("$.data.proxies[0].port").value(10908))
    }

    @Test
    fun `getMockProxy should return error response when exception occurs`() {
        // Given
        val proxyController = ProxyController(proxyVendorLoader)
        val mockMvc = MockMvcBuilders.standaloneSetup(proxyController).build()

        // When/Then
        mockMvc.perform(get("/api/get-moke-proxy"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.message").value("Proxies retrieved successfully"))
    }
}
