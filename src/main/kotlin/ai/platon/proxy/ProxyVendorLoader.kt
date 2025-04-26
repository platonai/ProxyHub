package ai.platon.proxy

import ai.platon.proxy.vendor.ProxyVendorFactory
import ai.platon.pulsar.common.*
import ai.platon.pulsar.common.config.ImmutableConfig
import ai.platon.pulsar.common.proxy.ProxyEntry
import ai.platon.pulsar.common.proxy.ProxyException
import ai.platon.pulsar.common.proxy.ProxyLoader
import ai.platon.pulsar.common.urls.UrlUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.net.SocketException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

/**
 * Load proxies from proxy vendors
 */
open class ProxyVendorLoader(conf: ImmutableConfig): ProxyLoader(conf) {

    private val logger = LoggerFactory.getLogger(ProxyVendorLoader::class.java)
    private val providers = mutableSetOf<String>()
    private val proxyChunkSize = 1
    private val minimumFetchInterval = Duration.ofSeconds(3)
    private var numLoadedProxies = 0
    @Volatile
    private var lastFetchTime = Instant.EPOCH

    @Throws(ProxyException::class)
    override fun updateProxies(reloadInterval: Duration): List<ProxyEntry> {
        return loadEnabledProviders(reloadInterval).toCollection(providers)
            .flatMap { fetchProxiesFromProvider(it) }
    }

    fun fetchProxiesFromProvider(providerUrl: String): List<ProxyEntry> {
        val space = StringUtils.SPACE
        var url = providerUrl.substringBefore(space)
        val metadata = providerUrl.substringAfter(space)
        var vendor = "none"
        var format = "txt"

        metadata.split(space).zipWithNext().forEach {
            when (it.first) {
                "-vendor" -> vendor = it.second
                "-fmt" -> format = it.second
            }
        }

        if (vendor == "mock") {
            val path = AppPaths.AVAILABLE_PROXY_DIR.resolve("mock_proxy.json")
            return parseQualifiedProxies(path, vendor, format)
        }

        if (Duration.between(lastFetchTime, Instant.now()) < minimumFetchInterval) {
            sleepSeconds(minimumFetchInterval.seconds)
        }
        lastFetchTime = Instant.now()

        return kotlin.runCatching { fetchProxiesFromProvider(URL(url), vendor, format) }
            .onFailure { warnInterruptible(this, it) }
            .getOrNull() ?: listOf()
    }

    @Synchronized
    @Throws(SocketException::class)
    fun fetchProxiesFromProvider(providerURL: URL, vendor: String = "none", format: String = "txt"): List<ProxyEntry> {
        val filename = "proxies." + AppPaths.fromUri(providerURL.toString()) + "." + vendor + "." + format
        val target = AppPaths.PROXY_ARCHIVE_DIR.resolve(filename)

        if (!isActive) {
            return listOf()
        }

        logger.info("Fetching proxies from provider | {}", providerURL)

        Files.deleteIfExists(target)
        FileUtils.copyURLToFile(providerURL, target.toFile())

        return parseQualifiedProxies(target, vendor, format)
    }

    fun loadEnabledProviders(reloadInterval: Duration): List<String> {
        return Files.list(AppPaths.ENABLED_PROVIDER_DIR).filter { Files.isRegularFile(it) }
            .collect(Collectors.toList())
            .flatMap { loadIfModified(it, reloadInterval) { loadProviders(it) } }
    }

    fun loadProviders(path: Path): List<String> {
        return Files.readAllLines(path).map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .distinct().shuffled().filter { UrlUtils.isStandard(it) }
    }

    /**
     * Parse qualified proxies from the file
     *
     * @param path the file to parse
     * @param vendor the vendor of the proxies
     * @param format the format of the file
     * @param test whether to test the proxies
     * @return the qualified proxies
     * */
    fun parseQualifiedProxies(
        path: Path,
        vendor: String = "none",
        format: String = "txt",
        test: Boolean = false
    ): List<ProxyEntry> {
        if (vendor == "mock") {
            return ProxyVendorFactory.getProxyParser(vendor).parse(path, format)
        }

        logger.info("Testing proxies, vendor: $vendor, format: $format | file://$path")
        val count = AtomicInteger()
        val unfilteredProxies = ProxyVendorFactory.getProxyParser(vendor).parse(path, format)
        val qualifiedProxies = unfilteredProxies.filter { check(it) }.shuffled().chunked(proxyChunkSize).flatMap {
                it.parallelStream()
                    .filter { isActive }
                    .filter { !test || canConnect(it) }
                    .map { it.also { logger.info("Test passed: ${it.display}"); count.incrementAndGet() } }
                    .toList()
            }

        numLoadedProxies += unfilteredProxies.size
        logger.info("Loaded qualified {}/{} proxies", qualifiedProxies.size, numLoadedProxies)

        return qualifiedProxies
    }

    private fun check(proxyEntry: ProxyEntry): Boolean {
        if (proxyEntry.willExpireAfter(minimumProxyTTL)) {
            logger.warn("Proxy will expire after {} | {}", minimumProxyTTL, proxyEntry)
            return false
        }
        
        if (proxyEntry.outSegment in bannedSegments) {
            logger.warn("Segment is banned | {}", proxyEntry)
            return false
        }
        
        if (proxyEntry.outIp in bannedIps) {
            logger.warn("Ip is banned | {}", proxyEntry)
            return false
        }
        
        return true
    }
    
    private fun canConnect(proxyEntry: ProxyEntry): Boolean {
        if (!isActive) {
            logger.warn("The system is shutdown")
            return false
        }
        
        if (!testProxyBeforeUse) {
            return true
        }
        
        val canConnect = proxyEntry.test(URL(testUrl))
        if (!canConnect) {
            logger.warn("Failed to connect to proxy | {}", testUrl)
        }
        
        return canConnect
    }
}
