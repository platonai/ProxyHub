package ai.platon.proxy.repository

import ai.platon.proxy.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>