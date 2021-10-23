package com.template.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

const val EMAIL = "test@test.com"
const val NAME = "testUserName"
const val PASSWORD = "testPassword"
const val USER_ID = 1
const val TOKEN = "token"
const val JWT_SECRET = "TestJwtSecretKey"
const val JWT_ACCESS_TOKEN_EXP = 86400000
const val JWT_REFRESH_TOKEN_EXP = 604800000
const val EXTRA_TIME = 2000000

fun generateExpiredToken(exp: Int, secret: String): String {
    val realExp = EXTRA_TIME + exp
    val claims: MutableMap<String, Any> = mutableMapOf()
    claims["userId"] = USER_ID
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(Date(System.currentTimeMillis() - realExp))
        .setExpiration(Date(System.currentTimeMillis() - EXTRA_TIME))
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact()
}

fun generateOtherSignatureToken(exp: Int): String {
    val claims: MutableMap<String, Any> = mutableMapOf()
    claims["userId"] = USER_ID
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + exp))
        .signWith(SignatureAlgorithm.HS256, "Other Signature")
        .compact()
}
