package eu.vitamoments.app.dbHelpers

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String, hashed: String): Boolean {
        val result = BCrypt.verifyer().verify(password.toCharArray(), hashed)
        return result.verified
    }
}