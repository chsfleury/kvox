package fr.chsfleury.kvox.impl

import fr.chsfleury.kvox.ColorARGB
import fr.chsfleury.kvox.ColorPalette

/**
 * The provided colour integers must be in the ARGB format, i.e.
 * the highest 8 bits represent the Alpha channel, and
 * the lowest 8 bits represent the blue channel.
 * Valid indices are from 1 to 255; 0 is not used.
 */
class MutableColorPalette(
    initialColors: Iterable<ColorARGB>
): ColorPalette {

    constructor(vararg colors: ColorARGB) : this(colors.asIterable())

    /**
     * The returned colour integers are in the ARGB format, i.e.
     * the highest 8 bits represent the Alpha channel, and
     * the lowest 8 bits represent the blue channel.
     * Valid indices are from 1 to 255; 0 is not used.
     */
    override val palette = IntArray(256)

    /** First index is 1, 0 is ignored */
    private var nextIndex = 1

    private val colors: MutableMap<Int, ColorARGB> = mutableMapOf()
    private val reverseColors: MutableMap<ColorARGB, Int> = mutableMapOf()

    init {
        initialColors.forEach(::plus)
    }

    operator fun plus(color: ColorARGB) {
        if (nextIndex >= palette.size) {
            throw IllegalStateException("palette max size reached (${palette.size})")
        }
        set(nextIndex++, color)
    }

    operator fun set(index: Int, color: ColorARGB) {
        colors[index] = color
        reverseColors[color] = index
        palette[index] = color.toInt()
    }

    operator fun get(index: Int): ColorARGB? = colors[index]
    operator fun get(color: ColorARGB): Int? = reverseColors[color]
}
