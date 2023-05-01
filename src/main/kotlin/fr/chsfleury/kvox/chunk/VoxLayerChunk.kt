package fr.chsfleury.kvox.chunk

import com.scs.voxlib.VLGridPoint3
import fr.chsfleury.kvox.utils.StreamUtils.readDictionary
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxLayerChunk: VoxChunk(ChunkFactory.LAYR) {

    var id = 0
    private var transform = VLGridPoint3()

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxLayerChunk {
            val chunk = VoxLayerChunk()
            chunk.id = stream.readIntLittleEndian()
            val dict = stream.readDictionary()
            val reserved = stream.readIntLittleEndian()
            return chunk
        }

    }

    override fun toString(): String {
        return "VoxLayerChunk#" + id + "_" + transform
    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        stream.writeIntLittleEndian(id)
        stream.writeIntLittleEndian(0)
        stream.writeIntLittleEndian(0)
    }

}