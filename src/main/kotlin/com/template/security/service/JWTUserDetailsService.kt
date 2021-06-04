package com.template.security.service

import com.template.common.function.AuthorizeUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class JWTUserDetailsService(private val authorizeUser: AuthorizeUser) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = authorizeUser.apply(Integer.parseInt(username!!))
        return UserDetailsImpl(user.id!!)
    }

    fun getAuthorities() : Set<GrantedAuthority> {
        return mutableSetOf()
    }
}