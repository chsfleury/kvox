package fr.chsfleury.kvox.chunk

import com.scs.voxlib.VLStreamUtils
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxRGBAChunk(initialPalette: IntArray? = null): VoxChunk(ChunkFactory.RGBA) {

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
                this.palette[i] = initialPalette[i]
                i++
            }
        }
    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        for (i in 0..254) {
            val abgr = ARGBToABGR(palette[i + 1])
            VLStreamUtils.writeIntLE(abgr, stream)
        }
    }

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxRGBAChunk {
            val chunk = VoxRGBAChunk()
            for (i in 0..254) {
                val abgr = stream.readIntLittleEndian()
                chunk.palette[i + 1] = ABGRToARGB(abgr)
            }
            return chunk
        }

        private fun ABGRToARGB(abgr: Int): Int {
            val alpha = abgr and -0x1000000 shr 24
            val blue = abgr and 0xFF0000 shr 16
            val green = abgr and 0xFF00 shr 8
            val red = abgr and 0xFF
            return blue or (green shl 8) or (red shl 16) or (alpha shl 24)
        }

        private fun ARGBToABGR(argb: Int): Int {
            val alpha = argb and -0x1000000 shr 24
            val red = argb and 0xFF0000 shr 16
            val green = argb and 0xFF00 shr 8
            val blue = argb and 0xFF
            return red or (green shl 8) or (blue shl 16) or (alpha shl 24)
        }

        val DEFAULT_PALETTE = intArrayOf(
            0x00000000, -0x1, -0x330001, -0x660001, -0x990001, -0xcc0001,
            -0xff0001, -0x3301, -0x333301, -0x663301, -0x993301, -0xcc3301,
            -0xff3301, -0x6601, -0x336601, -0x666601, -0x996601, -0xcc6601,
            -0xff6601, -0x9901, -0x339901, -0x669901, -0x999901, -0xcc9901,
            -0xff9901, -0xcc01, -0x33cc01, -0x66cc01, -0x99cc01, -0xcccc01,
            -0xffcc01, -0xff01, -0x33ff01, -0x66ff01, -0x99ff01, -0xccff01,
            -0xffff01, -0x34, -0x330034, -0x660034, -0x990034, -0xcc0034,
            -0xff0034, -0x3334, -0x333334, -0x663334, -0x993334, -0xcc3334,
            -0xff3334, -0x6634, -0x336634, -0x666634, -0x996634, -0xcc6634,
            -0xff6634, -0x9934, -0x339934, -0x669934, -0x999934, -0xcc9934,
            -0xff9934, -0xcc34, -0x33cc34, -0x66cc34, -0x99cc34, -0xcccc34,
            -0xffcc34, -0xff34, -0x33ff34, -0x66ff34, -0x99ff34, -0xccff34,
            -0xffff34, -0x67, -0x330067, -0x660067, -0x990067, -0xcc0067,
            -0xff0067, -0x3367, -0x333367, -0x663367, -0x993367, -0xcc3367,
            -0xff3367, -0x6667, -0x336667, -0x666667, -0x996667, -0xcc6667,
            -0xff6667, -0x9967, -0x339967, -0x669967, -0x999967, -0xcc9967,
            -0xff9967, -0xcc67, -0x33cc67, -0x66cc67, -0x99cc67, -0xcccc67,
            -0xffcc67, -0xff67, -0x33ff67, -0x66ff67, -0x99ff67, -0xccff67,
            -0xffff67, -0x9a, -0x33009a, -0x66009a, -0x99009a, -0xcc009a,
            -0xff009a, -0x339a, -0x33339a, -0x66339a, -0x99339a, -0xcc339a,
            -0xff339a, -0x669a, -0x33669a, -0x66669a, -0x99669a, -0xcc669a,
            -0xff669a, -0x999a, -0x33999a, -0x66999a, -0x99999a, -0xcc999a,
            -0xff999a, -0xcc9a, -0x33cc9a, -0x66cc9a, -0x99cc9a, -0xcccc9a,
            -0xffcc9a, -0xff9a, -0x33ff9a, -0x66ff9a, -0x99ff9a, -0xccff9a,
            -0xffff9a, -0xcd, -0x3300cd, -0x6600cd, -0x9900cd, -0xcc00cd,
            -0xff00cd, -0x33cd, -0x3333cd, -0x6633cd, -0x9933cd, -0xcc33cd,
            -0xff33cd, -0x66cd, -0x3366cd, -0x6666cd, -0x9966cd, -0xcc66cd,
            -0xff66cd, -0x99cd, -0x3399cd, -0x6699cd, -0x9999cd, -0xcc99cd,
            -0xff99cd, -0xcccd, -0x33cccd, -0x66cccd, -0x99cccd, -0xcccccd,
            -0xffcccd, -0xffcd, -0x33ffcd, -0x66ffcd, -0x99ffcd, -0xccffcd,
            -0xffffcd, -0x100, -0x330100, -0x660100, -0x990100, -0xcc0100,
            -0xff0100, -0x3400, -0x333400, -0x663400, -0x993400, -0xcc3400,
            -0xff3400, -0x6700, -0x336700, -0x666700, -0x996700, -0xcc6700,
            -0xff6700, -0x9a00, -0x339a00, -0x669a00, -0x999a00, -0xcc9a00,
            -0xff9a00, -0xcd00, -0x33cd00, -0x66cd00, -0x99cd00, -0xcccd00,
            -0xffcd00, -0x10000, -0x340000, -0x670000, -0x9a0000, -0xcd0000,
            -0xffff12, -0xffff23, -0xffff45, -0xffff56, -0xffff78, -0xffff89,
            -0xffffab, -0xffffbc, -0xffffde, -0xffffef, -0xff1200, -0xff2300,
            -0xff4500, -0xff5600, -0xff7800, -0xff8900, -0xffab00, -0xffbc00,
            -0xffde00, -0xffef00, -0x120000, -0x230000, -0x450000, -0x560000,
            -0x780000, -0x890000, -0xab0000, -0xbc0000, -0xde0000, -0xef0000,
            -0x111112, -0x222223, -0x444445, -0x555556, -0x777778, -0x888889,
            -0xaaaaab, -0xbbbbbc, -0xddddde, -0xeeeeef
        )

    }

}