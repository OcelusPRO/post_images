package yt.ftnl.core

import java.security.MessageDigest

/**
 * Hash string.
 *
 * @param algorithm [String] algorithm to hash.
 * @return [String] The hash of the token.
 */
fun String.hash(algorithm: String): String {
    val hexChars = "0123456789abcdef"
    val bytes = MessageDigest
        .getInstance(algorithm)
        .digest(this.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(hexChars[i shr 4 and 0x0f])
        result.append(hexChars[i and 0x0f])
    }

    return result.toString()
}