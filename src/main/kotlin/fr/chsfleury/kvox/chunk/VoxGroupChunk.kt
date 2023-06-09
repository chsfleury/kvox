package fr.chsfleury.kvox.chunk

import fr.chsfleury.kvox.utils.StreamUtils.readDictionary
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxGroupChunk(
    val id: Int,
    val childIds: List<Int>
): VoxChunk(ChunkFactory.nGRP) {

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxGroupChunk {
            val id = stream.readIntLittleEndian()
            stream.readDictionary()
            val numChildren = stream.readIntLittleEndian()
            val childIds = List(numChildren) {
                stream.readIntLittleEndian()
            }
            return VoxGroupChunk(id, childIds)
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

    override fun toString(): String {
        return "Group#${id}_children_" + childIds.joinToString("_")
    }
}