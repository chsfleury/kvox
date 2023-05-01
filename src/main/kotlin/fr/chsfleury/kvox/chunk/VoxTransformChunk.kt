package fr.chsfleury.kvox.chunk

import com.scs.voxlib.VLGridPoint3
import fr.chsfleury.kvox.utils.StreamUtils.readDictionary
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeDictionary
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxTransformChunk(
    val id: Int
): VoxChunk(ChunkFactory.nTRN) {

    var childNodeId = 0
    var transform = VLGridPoint3()

    companion object {
        private val SPACE_REGEX = Regex(" ")

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxTransformChunk {
            val id = stream.readIntLittleEndian()
            val chunk = VoxTransformChunk(id)
            val dict = stream.readDictionary()
            chunk.childNodeId = stream.readIntLittleEndian()
            val neg1 = stream.readIntLittleEndian()
            if (neg1 != -1) {
                throw RuntimeException("neg1 checksum failed")
            }
            val layerId = stream.readIntLittleEndian()
            val numFrames = stream.readIntLittleEndian()

            // Rotation
            for (i in 0 until numFrames) {
                val rot = stream.readDictionary()
                if (rot.containsKey("_t")) {
                    val tokens = rot["_t"]!!
                        .split(SPACE_REGEX)
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()

                    val tmp = VLGridPoint3(tokens[0].toInt(), tokens[1].toInt(), tokens[2].toInt())
                    chunk.transform.set(tmp.x, tmp.y, tmp.z)
                }
            }
            return chunk
        }

    }

    override fun toString(): String {
        return "VoxTransformChunk#" + id + "_" + transform
    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        stream.writeIntLittleEndian(id)
        stream.writeIntLittleEndian(0)
        stream.writeIntLittleEndian(childNodeId)
        stream.writeIntLittleEndian(-1)
        stream.writeIntLittleEndian(0)
        if (transform.x != 0 || transform.y != 0 || transform.z != 0) {
            stream.writeIntLittleEndian(1)
            val rot = HashMap<String, String>()
            rot["_t"] = String.format("%d %d %d", transform.x, transform.y, transform.z)
            stream.writeDictionary(rot)
        } else {
            stream.writeIntLittleEndian(0)
        }
    }

}