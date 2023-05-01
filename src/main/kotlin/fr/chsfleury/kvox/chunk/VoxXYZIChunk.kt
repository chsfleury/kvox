package fr.chsfleury.kvox.chunk

import com.scs.voxlib.StreamUtils
import com.scs.voxlib.Voxel
import com.scs.voxlib.chunk.VLChunkFactory
import com.scs.voxlib.chunk.VLVoxXYZIChunk
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.readVector3b
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeVector3b
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxXYZIChunk(
    val voxels: Array<Voxel?>
): VoxChunk(ChunkFactory.XYZI) {

    constructor(voxelCount: Int) : this(arrayOfNulls<Voxel>(voxelCount))
    constructor(voxels: Collection<Voxel?>) : this(voxels.toTypedArray())

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxXYZIChunk {
            val voxelCount = stream.readIntLittleEndian()
            val chunk = VoxXYZIChunk(voxelCount)
            for (i in 0 until voxelCount) {
                val position = stream.readVector3b()
                val colorIndex = (stream.read() and 0xff).toByte()
                chunk.voxels[i] = Voxel(position, colorIndex)
            }
            return chunk
        }

    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        stream.writeIntLittleEndian(voxels.size)
        for (voxel in voxels) {
            stream.writeVector3b(voxel!!.position)
            stream.write(voxel.colourIndex)
        }
    }

}