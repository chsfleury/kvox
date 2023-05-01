package fr.chsfleury.kvox.chunk

import com.scs.voxlib.VLGridPoint3
import fr.chsfleury.kvox.utils.StreamUtils.readVector3i
import fr.chsfleury.kvox.utils.StreamUtils.writeVector3i
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxSizeChunk(val size: VLGridPoint3): VoxChunk(ChunkFactory.SIZE) {

    constructor(width: Int, length: Int, height: Int) : this(VLGridPoint3(width, length, height))

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream) = VoxSizeChunk(stream.readVector3i())

    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        stream.writeVector3i(size)
    }
}