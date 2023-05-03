package fr.chsfleury.kvox.chunk

import fr.chsfleury.kvox.Vec3
import fr.chsfleury.kvox.utils.StreamUtils.readDictionary
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxLayerChunk(val id: Int): VoxChunk(ChunkFactory.LAYR) {
    private var transform = Vec3.ORIGIN

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxLayerChunk {
            val id = stream.readIntLittleEndian()
            stream.readDictionary()
            stream.readIntLittleEndian()
            return VoxLayerChunk(id)
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