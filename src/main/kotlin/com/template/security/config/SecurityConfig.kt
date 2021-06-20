package com.template.security.config

import com.template.auth.tools.JwtTokenUtil
import com.template.security.filter.JWTRequestFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtTokenUtil: JwtTokenUtil, private val authenticationEntryPoint: AuthenticationEntryPoint): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        // TODO: JWT 인증 절차를 추가할 End point 추가 및 수정
        http!!
        http
            .httpBasic().disable()
            .headers().frameOptions().disable()
            .and()
            .csrf().disable()
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .logout().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/v1/**")
            .authenticated()
            .and()
            .addFilterBefore(JWTRequestFilter(jwtTokenUtil, authenticationEntryPoint), UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
    }

    override fun configure(web: WebSecurity?) {
        // TODO: JWT 인증 절차를 제외할 End point 추가 및 수정
        web?.ignoring()
            ?.mvcMatchers(HttpMethod.POST, "/v1/auth/update-token")
            ?.mvcMatchers(HttpMethod.POST, "/v1/auth/login")
            ?.mvcMatchers(HttpMethod.GET, "/swagger-ui/**")
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOriginPattern("*")
        configuration.addAllowedHeader("*")
        configuration.addAllowedMethod("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}