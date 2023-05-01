package fr.chsfleury.kvox.chunk

import com.scs.voxlib.StreamUtils
import fr.chsfleury.kvox.utils.StreamUtils.readDictionary
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxShapeChunk(
    val id: Int
): VoxChunk(ChunkFactory.nSHP) {

    val modelIds: MutableList<Int> = mutableListOf()

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxShapeChunk {
            val id = stream.readIntLittleEndian()
            val chunk = VoxShapeChunk(id)
            val dict = stream.readDictionary()
            val numModels = stream.readIntLittleEndian()
            for (i in 0 until numModels) {
                val modelId = stream.readIntLittleEndian()
                val dict = stream.readDictionary()
                chunk.modelIds.add(modelId)
            }
            return chunk
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