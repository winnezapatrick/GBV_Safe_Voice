package com.example.gbvapp.data.firebase

import java.security.SecureRandom

object ReferenceGenerator {
    private const val CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    private val random = SecureRandom()

    fun generateReference(): String {
        val part1 = StringBuilder(4)
        val part2 = StringBuilder(4)
        repeat(4) {
            part1.append(CHARS[random.nextInt(CHARS.length)])
            part2.append(CHARS[random.nextInt(CHARS.length)])
        }
        return "SV-$part1-$part2"
    }
}
