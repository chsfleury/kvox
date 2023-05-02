package fr.chsfleury.kvox.chunk

import fr.chsfleury.kvox.utils.StreamUtils.readDictionary
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxShapeChunk(
    val id: Int,
    val modelIds: List<Int>
): VoxChunk(ChunkFactory.nSHP) {

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxShapeChunk {
            val id = stream.readIntLittleEndian()
            stream.readDictionary()
            val numModels = stream.readIntLittleEndian()
            val modelIds = List(numModels) {
                stream.readIntLittleEndian().also {
                    stream.readDictionary()
                }
            }
            return VoxShapeChunk(id, modelIds)
        }

    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        stream.writeIntLittleEndian(id)
        stream.writeIntLittleEndian(0)
        stream.writeIntLittleEndian(modelIds.size)
        for (modelId in modelIds) {
            stream.writeIntLittleEndian(modelId)
            stream.writeIntLittleEndian(0)
        }
    }
}