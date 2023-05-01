package fr.chsfleury.kvox.chunk

import com.scs.voxlib.mat.VLVoxMaterial
import fr.chsfleury.kvox.utils.StreamUtils.readDictionary
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeDictionary
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxMATLChunk(
    val material: VLVoxMaterial
): VoxChunk(ChunkFactory.MATL) {

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxMATLChunk {
            val id = stream.readIntLittleEndian()
            val props = stream.readDictionary()
            val material = VLVoxMaterial(id, props)
            return VoxMATLChunk(material)
        }

    }

    @Throws(IOException::class)
    override fun writeContent(stream: OutputStream) {
        stream.writeIntLittleEndian(material.id)
        stream.writeDictionary(material.props)
    }

}