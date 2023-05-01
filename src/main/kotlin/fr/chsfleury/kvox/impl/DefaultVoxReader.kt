package fr.chsfleury.kvox.impl

import fr.chsfleury.kvox.InvalidVoxException
import fr.chsfleury.kvox.VoxReader
import fr.chsfleury.kvox.chunk.VoxChunk
import fr.chsfleury.kvox.chunk.VoxRootChunk
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import java.io.InputStream

class DefaultVoxReader(private val inputStream: InputStream): VoxReader<VoxRootChunk> {
    companion object {
        const val VOX_FORMAT_VERSION = 150
        val MAGIC_BYTES = "VOX ".toByteArray()
    }

    override fun read(): DefaultVoxFile {
        checkMagicBytes()
        val voxFileVersion = checkVersion()

        val voxChunk = VoxChunk.readChunk(inputStream) ?: throw InvalidVoxException("No root chunk present in the file")
        val rootChunk = voxChunk as? VoxRootChunk ?: throw InvalidVoxException("First chunk is not of ID 'MAIN'")
        return DefaultVoxFile(voxFileVersion, rootChunk)
    }

    private fun checkMagicBytes() {
        val magicBytes = ByteArray(MAGIC_BYTES.size)
        if (inputStream.read(magicBytes) != MAGIC_BYTES.size) {
            throw InvalidVoxException("Could not read magic bytes")
        }

        if (!magicBytes.contentEquals(MAGIC_BYTES)) {
            throw InvalidVoxException("Invalid magic bytes")
        }
    }

    private fun checkVersion(): Int {
        val voxFileVersion = inputStream.readIntLittleEndian()
        if (voxFileVersion < VOX_FORMAT_VERSION) {
            throw InvalidVoxException("Vox versions older than $VOX_FORMAT_VERSION are not supported")
        }
        return voxFileVersion
    }

    override fun close() = inputStream.close()
}