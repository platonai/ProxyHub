package ai.platon.proxy.controller

import ai.platon.proxy.model.User
import ai.platon.proxy.repository.UserRepository
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val repo: UserRepository) {

    @GetMapping fun all(): List<User> = repo.findAll()

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<User> =
        repo.findById(id).map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @PostMapping fun create(@RequestBody user: User): User = repo.save(user)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody user: User): ResponseEntity<User> =
        repo.findById(id).map {
            val updated = it.copy(name = user.name, email = user.email)
            ResponseEntity.ok(repo.save(updated))
        }.orElse(ResponseEntity.notFound().build())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        if (repo.existsById(id)) {
            repo.deleteById(id)
            ResponseEntity.noContent().build()
        } else ResponseEntity.notFound().build()
}
