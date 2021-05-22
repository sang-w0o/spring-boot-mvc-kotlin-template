package com.template.domain.user

import com.template.domain.common.CreatedAtEntity
import javax.persistence.*

@Entity
@Table(name = "users")
class User(name: String, email: String, password: String): CreatedAtEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "user_id")
    val id: Int? = null

    @Column(nullable = false, length = 100)
    var name: String = name

    @Column(length = 60)
    var password: String? = password

    @Column(nullable = false, length = 100, unique = true)
    var email: String = email
}