package fr.chsfleury.kvox.chunk

import fr.chsfleury.kvox.ColorPalette
import fr.chsfleury.kvox.impl.IntArrayColorPalette
import fr.chsfleury.kvox.utils.IntBytes
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxRGBAChunk(private val colorPalette: ColorPalette): VoxChunk(ChunkFactory.RGBA) {

    val palette: IntArray get() = colorPalette.palette

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        for (i in 1..255) {
            stream.writeIntLittleEndian(convertARGBToABGR(colorPalette.palette[i]))
        }
    }

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxRGBAChunk {
            val palette = IntArray(256) { index ->
                if (index == 0) 0
                else convertABGRToARGB(stream.readIntLittleEndian())
            }
            return VoxRGBAChunk(IntArrayColorPalette(palette))
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