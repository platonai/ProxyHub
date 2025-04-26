package ai.platon.proxy.repository

import ai.platon.proxy.model.ProxyProvider
import org.springframework.data.jpa.repository.JpaRepository

interface ProxyProviderRepository : JpaRepository<ProxyProvider, Long>