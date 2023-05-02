package fr.chsfleury.kvox.utils

data class IntBytes(
    val a: Int,
    val b: Int,
    val c: Int,
    val d: Int
) {
    constructor(source: Int) : this(
        source and -0x1000000 shr 24,
        source and 0xFF0000 shr 16,
        source and 0xFF00 shr 8,
        source and 0xFF
    )

    companion object {
        fun toInt(a: Int, b: Int, c: Int, d: Int) = a or (b shl 8) or (c shl 16) or (d shl 24)
    }
}
