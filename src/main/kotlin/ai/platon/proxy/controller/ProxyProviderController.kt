package ai.platon.proxy.controller

import ai.platon.proxy.model.ProxyProvider
import ai.platon.proxy.repository.ProxyProviderRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/providers")
class ProxyProviderController(private val repo: ProxyProviderRepository) {

    @GetMapping
    fun all(): List<ProxyProvider> = repo.findAll()

    @PostMapping
    fun create(@RequestBody p: ProxyProvider): ResponseEntity<Any> {
        repo.save(p)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<ProxyProvider> =
        repo.findById(id).map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        if (repo.existsById(id)) {
            repo.deleteById(id)
            ResponseEntity.noContent().build()
        } else ResponseEntity.notFound().build()
}
