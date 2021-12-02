package org.ergoplatform

import kotlin.math.pow

class TokenAmount(val rawValue: Long, val decimals: Int) {

    constructor(tokenString: String, decimals: Int) : this(
        if (tokenString.isBlank()) 0
        else tokenString.toBigDecimal().movePointRight(decimals).longValueExact(), decimals
    )

    override fun toString(): String {
        return rawValue.toBigDecimal().movePointLeft(decimals).toPlainString()
    }

    fun toStringTrimTrailingZeros(): String {
        val stringWithTrailingZeros = toString()
        if (decimals > 0) {
            return stringWithTrailingZeros.trimEnd('0').trimEnd('.')
        } else {
            return stringWithTrailingZeros
        }
    }

    fun toDouble(): Double {
        return (rawValue.toDouble()) / (10.0.pow(decimals))
    }
}

fun String.toTokenAmount(decimals: Int): TokenAmount? {
    try {
        return TokenAmount(this, decimals)
    } catch (t: Throwable) {
        return null
    }
}