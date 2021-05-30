package com.template.security.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    private val id: Int
): UserDetails {

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return ""
    }

    override fun getUsername(): String {
        return id.toString()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = mutableSetOf<SimpleGrantedAuthority>()
        authorities.add(SimpleGrantedAuthority("ROLE_USER"))
        return authorities
    }
}