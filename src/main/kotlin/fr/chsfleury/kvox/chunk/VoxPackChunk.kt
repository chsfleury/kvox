package fr.chsfleury.kvox.chunk

import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxPackChunk(
    private val modelCount: Int
): VoxChunk(ChunkFactory.PACK) {

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) = stream.writeIntLittleEndian(modelCount)

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream) = VoxPackChunk(stream.readIntLittleEndian())

    }

}