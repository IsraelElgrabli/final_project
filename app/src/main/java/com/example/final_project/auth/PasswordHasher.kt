package com.example.final_project.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordHasher {
    private val rng = SecureRandom()

    fun newSalt(bytes: Int = 16): String {
        val b = ByteArray(bytes); rng.nextBytes(b)
        return Base64.getEncoder().encodeToString(b)
    }

    fun hash(password: String, saltBase64: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(Base64.getDecoder().decode(saltBase64))
        val out = md.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(out)
    }
}
