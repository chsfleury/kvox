package fr.chsfleury.kvox

import fr.chsfleury.kvox.utils.IntBytes

data class ColorARGB(
    val red: Int = 255,
    val green: Int = 255,
    val blue: Int = 255,
    val alpha: Int = 255
) {
    fun toInt() = IntBytes.toInt(blue, green, red, alpha)

    companion object {
        fun of(source: Int): ColorARGB {
            val (a, r, g ,b) = IntBytes(source)
            return ColorARGB(r, g, b, a)
        }
    }
}
