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
    constructor(id: Int, numModels: Int, init: (Int) -> Int) : this(id, List(numModels, init))

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxShapeChunk {
            val id = stream.readIntLittleEndian()
            stream.readDictionary()
            val numModels = stream.readIntLittleEndian()
            return VoxShapeChunk(id, numModels) {
                stream.readIntLittleEndian().also {
                    stream.readDictionary() // ignored
                }
            }
        }

    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        stream
            .writeIntLittleEndian(id)
            .writeIntLittleEndian(0)
            .writeIntLittleEndian(modelIds.size)
        for (modelId in modelIds) {
            stream
                .writeIntLittleEndian(modelId)
                .writeIntLittleEndian(0)
        }
    }
}