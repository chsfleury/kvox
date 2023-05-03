package fr.chsfleury.kvox.chunk

import fr.chsfleury.kvox.Vec3
import fr.chsfleury.kvox.utils.StreamUtils.readDictionary
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeDictionary
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxTransformChunk(
    val id: Int,
    val childNodeId: Int,
    val transform: Vec3 = Vec3.ORIGIN
): VoxChunk(ChunkFactory.nTRN) {

    companion object {
        private val SPACE_REGEX = Regex(" ")

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxTransformChunk {
            val id = stream.readIntLittleEndian()
            stream.readDictionary() // ignored
            val childNodeId = stream.readIntLittleEndian()
            val neg1 = stream.readIntLittleEndian()
            if (neg1 != -1) {
                throw RuntimeException("neg1 checksum failed")
            }
            stream.readIntLittleEndian() // ignored layerId
            val numFrames = stream.readIntLittleEndian()

            var transform = Vec3.ORIGIN
            // Rotation
            for (i in 0 until numFrames) {
                val rot = stream.readDictionary()
                if (rot.containsKey("_t")) {
                    val tokens = rot["_t"]!!
                        .split(SPACE_REGEX)
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()

                    transform = Vec3(tokens[0].toInt(), tokens[1].toInt(), tokens[2].toInt())
                }
            }
            return VoxTransformChunk(id, childNodeId, transform)
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