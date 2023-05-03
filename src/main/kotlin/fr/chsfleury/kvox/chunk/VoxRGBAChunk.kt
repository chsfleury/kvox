package fr.chsfleury.kvox.chunk

import fr.chsfleury.kvox.utils.IntBytes
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxRGBAChunk(
    initialPalette: IntArray? = null
): VoxChunk(ChunkFactory.RGBA) {

    /**
     * The returned colour integers are in the ARGB format, i.e.
     * the highest 8 bits represent the Alpha channel, and
     * the lowest 8 bits represent the blue channel.
     * Valid indices are from 1 to 255; 0 is not used.
     */
    val palette = IntArray(256)

    /**
     * The provided colour integers must be in the ARGB format, i.e.
     * the highest 8 bits represent the Alpha channel, and
     * the lowest 8 bits represent the blue channel.
     * Valid indices are from 1 to 255; 0 is not used.
     */
    init {
        if (initialPalette != null) {
            var i = 1
            while (i < 256 && i < initialPalette.size) {
                this.palette[i] = initialPalette[i++]
            }
        }
    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        for (i in 1..255) {
            stream.writeIntLittleEndian(convertARGBToABGR(palette[i]))
        }
    }

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxRGBAChunk {
            val palette = IntArray(256) { index ->
                if (index == 0) 0
                else convertABGRToARGB(stream.readIntLittleEndian())
            }
            return VoxRGBAChunk(palette)
        }

        private fun convertABGRToARGB(abgr: Int): Int {
            val (alpha, blue, green, red) = IntBytes(abgr)
            return IntBytes.toInt(blue, green, red, alpha)
        }

        private fun convertARGBToABGR(argb: Int): Int {
            val (alpha, red, green, blue) = IntBytes(argb)
            return IntBytes.toInt(red, green, blue, alpha)
        }

    }

}