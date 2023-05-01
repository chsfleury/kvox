package fr.chsfleury.kvox.chunk

import com.scs.voxlib.StreamUtils
import fr.chsfleury.kvox.utils.StreamUtils.readDictionary
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxGroupChunk(
    val id: Int
): VoxChunk(ChunkFactory.nGRP) {

    var childIds: MutableList<Int> = mutableListOf()

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxGroupChunk {
            val id = stream.readIntLittleEndian()
            val chunk = VoxGroupChunk(id)
            val dict = stream.readDictionary()
            val numChildren = stream.readIntLittleEndian()
            for (i in 0 until numChildren) {
                val childId = stream.readIntLittleEndian()
                chunk.childIds.add(childId)
            }
            return chunk
        }

    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        stream.writeIntLittleEndian(id)
        stream.writeIntLittleEndian(0)
        stream.writeIntLittleEndian(childIds.size)
        for (childId in childIds) {
            stream.writeIntLittleEndian(childId)
        }
    }
}