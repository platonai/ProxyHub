package ai.platon.proxy.model

import jakarta.persistence.*

@Entity
data class ProxyProvider(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0,
    @Column(nullable = false, unique = true)
    val url: String = "",
)
